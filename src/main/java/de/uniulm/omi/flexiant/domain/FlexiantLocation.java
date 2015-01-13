package de.uniulm.omi.flexiant.domain;

import de.uniulm.omi.flexiant.extility.Cluster;
import de.uniulm.omi.flexiant.extility.Vdc;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class FlexiantLocation extends AbstractFlexiantResource {

    private final FlexiantLocationScope locationScope;

    @Nullable
    private final FlexiantLocation parent;

    public static FlexiantLocation from(Cluster cluster) {
        checkNotNull(cluster);
        return new FlexiantLocation(cluster);
    }

    public static FlexiantLocation from(Vdc vdc, Cluster cluster) {
        checkNotNull(vdc);
        checkNotNull(cluster);
        return new FlexiantLocation(vdc, cluster);
    }


    private FlexiantLocation(final Vdc vdc, final Cluster cluster) {
        super(vdc);
        this.locationScope = FlexiantLocationScope.VDC;
        this.parent = FlexiantLocation.from(cluster);
    }

    private FlexiantLocation(final Cluster cluster) {
        super(cluster);
        this.locationScope = FlexiantLocationScope.CLUSTER;
        this.parent = null;
    }

    @Nullable
    public FlexiantLocation getParent() {
        return this.parent;
    }

    public FlexiantLocationScope getLocationScope() {
        return locationScope;
    }
}
