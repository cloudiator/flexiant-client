package de.uniulm.omi.flexiant.domain.impl.generic;

import de.uniulm.omi.flexiant.domain.api.Resource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 05.12.14.
 */
public abstract class ResourceImpl implements Resource {

    protected final de.uniulm.omi.flexiant.extility.Resource resource;

    public ResourceImpl(de.uniulm.omi.flexiant.extility.Resource resource) {
        checkNotNull(resource);
        this.resource = resource;
    }

    @Override
    public String getId() {
        return this.resource.getResourceUUID();
    }

    @Override
    public String getName() {
        return this.resource.getResourceName();
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Resource && this.getId().equals(((Resource) obj).getId());
    }

    @Override
    public String toString() {
        return String.format("FlexiantResource{uuid=%s}", this.getId());
    }
}
