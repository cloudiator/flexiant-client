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

import de.uniulm.omi.cloudiator.flexiant.client.domain.generic.ResourceImpl;
import de.uniulm.omi.flexiant.extility.Cluster;
import de.uniulm.omi.flexiant.extility.Vdc;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class Location extends ResourceImpl {

    private final LocationScope locationScope;

    @Nullable
    private final Location parent;

    public static Location from(Cluster cluster) {
        checkNotNull(cluster);
        return new Location(cluster);
    }

    public static Location from(Vdc vdc, Cluster cluster) {
        checkNotNull(vdc);
        checkNotNull(cluster);
        return new Location(vdc, cluster);
    }

    private Location(final Vdc vdc, final Cluster cluster) {
        super(vdc);
        this.locationScope = LocationScope.VDC;
        this.parent = Location.from(cluster);
    }

    private Location(final Cluster cluster) {
        super(cluster);
        this.locationScope = LocationScope.CLUSTER;
        this.parent = null;
    }

    @Nullable
    public Location getParent() {
        return this.parent;
    }

    public LocationScope getLocationScope() {
        return locationScope;
    }
}
