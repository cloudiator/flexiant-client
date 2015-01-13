package de.uniulm.omi.flexiant.domain.impl;

import de.uniulm.omi.flexiant.domain.impl.generic.ResourceInLocationImpl;

import javax.annotation.Nullable;

public class Image extends ResourceInLocationImpl {

    public Image(final de.uniulm.omi.flexiant.extility.Image image) {
        super(image);
    }

    protected de.uniulm.omi.flexiant.extility.Image getImage() {
        return (de.uniulm.omi.flexiant.extility.Image) this.resource;
    }

    @Nullable
    public String getDefaultUser() {
        return getImage().getDefaultUser();
    }

    public boolean isGenPassword() {
        return getImage().isGenPassword();
    }
}
