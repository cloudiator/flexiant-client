package de.uniulm.omi.flexiant.domain.impl.generic;

import de.uniulm.omi.flexiant.domain.api.ResourceInLocation;
import de.uniulm.omi.flexiant.extility.VirtualResource;

/**
 * Created by daniel on 13.01.15.
 */
public abstract class ResourceInLocationImpl extends ResourceImpl implements ResourceInLocation {

    public ResourceInLocationImpl(VirtualResource virtualResource) {
        super(virtualResource);
    }

    @Override
    public String getVdcUUID() {
        return ((VirtualResource) this.resource).getVdcUUID();
    }

    @Override
    public String getClusterUUID() {
        return ((VirtualResource) this.resource).getClusterUUID();
    }
}
