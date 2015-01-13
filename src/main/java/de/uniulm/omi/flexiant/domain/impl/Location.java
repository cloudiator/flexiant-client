package de.uniulm.omi.flexiant.domain.impl;

import de.uniulm.omi.flexiant.domain.impl.generic.ResourceImpl;
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
