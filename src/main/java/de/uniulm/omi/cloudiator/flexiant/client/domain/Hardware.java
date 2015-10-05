/*
 * Copyright (c) 2014-2015 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.flexiant.client.domain;

import de.uniulm.omi.cloudiator.flexiant.client.api.ResourceInLocation;
import io.github.cloudiator.flexiant.extility.Cluster;
import io.github.cloudiator.flexiant.extility.ProductComponent;
import io.github.cloudiator.flexiant.extility.ProductOffer;
import io.github.cloudiator.flexiant.extility.Value;


import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Hardware implements ResourceInLocation {

    private static final String DISK_KEY = "size";
    private static final String RAM_KEY = "ram";
    private static final String CPU_KEY = "cpu";

    private final ProductOffer disk;
    private final ProductOffer machine;
    private final String locationUUID;

    public static Hardware from(final ProductOffer machine, final ProductOffer disk,
        final String locationUUID) {
        return new Hardware(machine, disk, locationUUID);
    }

    public static Set<Hardware> from(final List<ProductOffer> offers,
        final List<Cluster> availableClusters) {

        checkNotNull(offers);
        checkNotNull(availableClusters);

        Set<ProductOffer> machineOffers = new HashSet<ProductOffer>();
        Set<ProductOffer> diskOffers = new HashSet<ProductOffer>();

        List<String> availableStringClusters = new ArrayList<>(availableClusters.size());
        availableStringClusters.addAll(
            availableClusters.stream().map(Cluster::getResourceUUID).collect(Collectors.toList()));

        for (ProductOffer productOffer : offers) {
            for (ProductComponent productComponent : productOffer.getComponentConfig()) {
                for (Value value : productComponent.getProductConfiguredValues()) {

                    if (value.getKey().equals(DISK_KEY) && value.getValue() != null) {
                        diskOffers.add(productOffer);
                    }

                    if ((value.getKey().equals(RAM_KEY) || value.getKey().equals(CPU_KEY))
                        && value.getValue() != null) {
                        machineOffers.add(productOffer);
                    }
                }
            }
        }

        Set<Hardware> hardware = new HashSet<>();
        for (ProductOffer machineOffer : machineOffers) {
            for (ProductOffer diskOffer : diskOffers) {

                List<String> machineOfferClusters;
                if (machineOffer.getClusters().isEmpty()) {
                    machineOfferClusters = availableStringClusters;
                } else {
                    machineOfferClusters = machineOffer.getClusters();
                }

                List<String> diskOfferClusters;
                if (diskOffer.getClusters().isEmpty()) {
                    diskOfferClusters = availableStringClusters;
                } else {
                    diskOfferClusters = diskOffer.getClusters();
                }

                //intersect them
                machineOfferClusters.retainAll(diskOfferClusters);
                //assign to new variable
                List<String> clustersForBoth = new ArrayList<>(machineOfferClusters);

                hardware.addAll(clustersForBoth.stream()
                    .map(locationUUID -> Hardware.from(machineOffer, diskOffer, locationUUID))
                    .collect(Collectors.toList()));
            }
        }

        return hardware;
    }

    private Hardware(final ProductOffer machine, final ProductOffer disk,
        final String locationUUID) {

        checkNotNull(machine);
        checkNotNull(disk);
        checkNotNull(locationUUID);
        checkArgument(!locationUUID.isEmpty());

        checkArgument(this.searchForValueInProductOffer(CPU_KEY, machine) != null,
            "Machine Offer does not contain cpu key.");
        checkArgument(this.searchForValueInProductOffer(RAM_KEY, machine) != null,
            "Machine Offer does not contain ram key.");
        checkArgument(this.searchForValueInProductOffer(DISK_KEY, disk) != null,
            "Disk Offer does not contain disk key.");

        checkArgument(
            machine.getClusters().isEmpty() || machine.getClusters().contains(locationUUID));
        checkArgument(disk.getClusters().isEmpty() || disk.getClusters().contains(locationUUID));

        this.machine = machine;
        this.disk = disk;
        this.locationUUID = locationUUID;
    }

    @Override public String getId() {
        return machine.getResourceUUID() + ":" + disk.getResourceUUID();
    }

    @Override public boolean equals(Object obj) {
        return obj instanceof Hardware && ((Hardware) obj).getId().equals(this.getId())
            && ((Hardware) obj).getLocationUUID().equals(this.getLocationUUID());
    }

    @Override public int hashCode() {
        return (this.getId() + "/" + this.getLocationUUID()).hashCode();
    }

    @Override public String getName() {
        return "CPU" + this.getCores() + "RAM" + this.getRam();
    }

    @Override public String toString() {
        return String.format("FlexiantHardware{cores=%d, ram=%d}", this.getCores(), this.getRam());
    }

    public int getCores() {
        String value = this.searchForValueInProductOffer(CPU_KEY, this.machine);
        checkNotNull(value, "Machine offer does not contain cpu key.");
        return Integer.parseInt(value);
    }

    public int getRam() {
        String value = this.searchForValueInProductOffer(RAM_KEY, this.machine);
        checkNotNull(value, "Machine offer does not contain ram key.");
        return Integer.parseInt(value);
    }

    public Float getDiskSpace() {
        String value = this.searchForValueInProductOffer(DISK_KEY, this.disk);
        checkNotNull(value, "Disk offer dos not contain disk key.");
        return Float.parseFloat(value);
    }

    @Nullable private String searchForValueInProductOffer(String key, ProductOffer productOffer) {

        checkNotNull(productOffer);
        checkNotNull(key);
        checkArgument(!key.isEmpty());

        for (ProductComponent productComponent : productOffer.getComponentConfig()) {
            for (Value value : productComponent.getProductConfiguredValues()) {
                if (value.getKey().equals(key)) {
                    return value.getValue();
                }
            }
        }
        return null;
    }

    @Override public String getLocationUUID() {
        return this.locationUUID;
    }
}
