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
    public String getLocationUUID() {
        return ((VirtualResource) this.resource).getClusterUUID();
    }
}
