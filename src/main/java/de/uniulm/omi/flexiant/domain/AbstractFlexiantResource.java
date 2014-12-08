package de.uniulm.omi.flexiant.domain;

import de.uniulm.omi.flexiant.domain.FlexiantResource;
import de.uniulm.omi.flexiant.extility.Resource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 05.12.14.
 */
public abstract class AbstractFlexiantResource implements FlexiantResource {

    protected final Resource resource;

    public AbstractFlexiantResource(Resource resource) {
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
        return obj instanceof FlexiantResource && this.getId().equals(((FlexiantResource) obj).getId());
    }

    @Override
    public String toString() {
        return String.format("FlexiantResource{uuid=%s}", this.getId());
    }
}
