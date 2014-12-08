package de.uniulm.omi.flexiant.domain;

import de.uniulm.omi.flexiant.extility.Image;

import javax.annotation.Nullable;

public class FlexiantImage extends AbstractFlexiantResource {

    public FlexiantImage(final Image image) {
        super(image);
    }

    protected Image getImage() {
        return (Image) this.resource;
    }

    @Nullable
    public String getDefaultUser() {
        return getImage().getDefaultUser();
    }

    public boolean isGenPassword() {
        return getImage().isGenPassword();
    }
}
