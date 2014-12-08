package de.uniulm.omi.flexiant.domain;

import de.uniulm.omi.flexiant.extility.ProductOffer;

public class FlexiantHardware implements FlexiantResource {

    private final ProductOffer disk;
    private final ProductOffer machine;

    public FlexiantHardware(final ProductOffer disk, final ProductOffer machine) {
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
        return super.hashCode();
    }

    @Override
    public String getName() {
        return "GenericFlexiantHardware";
    }

    public int getCores() {
        return 1;
    }

    public int getRam() {
        return 1;
    }
}
