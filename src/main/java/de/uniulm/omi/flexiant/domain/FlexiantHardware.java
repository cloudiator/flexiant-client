package de.uniulm.omi.flexiant.domain;

import de.uniulm.omi.flexiant.extility.ProductComponent;
import de.uniulm.omi.flexiant.extility.ProductOffer;
import de.uniulm.omi.flexiant.extility.Value;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

public class FlexiantHardware implements FlexiantResource {

    private static final String DISK_KEY = "size";
    private static final String RAM_KEY = "ram";
    private static final String CPU_KEY = "cpu";

    private final ProductOffer disk;
    private final ProductOffer machine;

    public static FlexiantHardware from(final ProductOffer machine, final ProductOffer disk) {
        return new FlexiantHardware(machine, disk);
    }

    public static Set<FlexiantHardware> from(final List<ProductOffer> offers) {

        checkNotNull(offers);

        Set<ProductOffer> machineOffers = new HashSet<ProductOffer>();
        Set<ProductOffer> diskOffers = new HashSet<ProductOffer>();

        for (ProductOffer productOffer : offers) {

            for (ProductComponent productComponent : productOffer.getComponentConfig()) {
                for (Value value : productComponent.getProductConfiguredValues()) {

                    if (value.getKey().equals(DISK_KEY) && value.getValue() != null) {
                        diskOffers.add(productOffer);
                    }

                    if ((value.getKey().equals(RAM_KEY) || value.getKey().equals(CPU_KEY)) && value.getValue() != null) {
                        machineOffers.add(productOffer);
                    }
                }
            }
        }

        Set<FlexiantHardware> hardware = new HashSet<FlexiantHardware>();
        for (ProductOffer machineOffer : machineOffers) {
            for (ProductOffer diskOffer : diskOffers) {
                hardware.add(FlexiantHardware.from(machineOffer, diskOffer));
            }
        }

        return hardware;
    }

    private FlexiantHardware(final ProductOffer machine, final ProductOffer disk) {

        checkNotNull(machine);
        checkNotNull(disk);

        checkArgument(this.searchForValueInProductOffer(CPU_KEY, machine) != null, "Machine Offer does not contain cpu key.");
        checkArgument(this.searchForValueInProductOffer(RAM_KEY, machine) != null, "Machine Offer does not contain ram key.");

        checkArgument(this.searchForValueInProductOffer(DISK_KEY, disk) != null, "Disk Offer does not contain disk key.");

        this.machine = machine;
        this.disk = disk;
    }

    @Override
    public String getId() {
        return machine.getResourceUUID() + ":" + disk.getResourceUUID();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FlexiantHardware && ((FlexiantHardware) obj).getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public String getName() {
        return "CPU" + this.getCores() + "RAM" + this.getRam();
    }

    @Override
    public String toString() {
        return String.format("FlexiantHardware{cores=%d, ram=%d}", this.getCores(), this.getRam());
    }

    public int getCores() {
        String value = this.searchForValueInProductOffer(CPU_KEY, this.machine);
        checkState(value != null, "Machine offer does not contain cpu key.");
        return Integer.parseInt(value);
    }

    public int getRam() {
        String value = this.searchForValueInProductOffer(RAM_KEY, this.machine);
        checkState(value != null, "Machine offer does not contain ram key.");
        return Integer.parseInt(value);
    }

    @Nullable
    private String searchForValueInProductOffer(String key, ProductOffer productOffer) {

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

}
