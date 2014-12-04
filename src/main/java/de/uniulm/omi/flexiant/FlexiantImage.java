package de.uniulm.omi.flexiant;

import de.uniulm.omi.flexiant.extility.Image;

import static com.google.common.base.Preconditions.checkNotNull;

public class FlexiantImage {
	
	private final Image image;
	
	public FlexiantImage(final Image image) {
		checkNotNull(image);
		this.image = image;
	}

	public String getId() {
		return image.getResourceUUID();
	}
	
	public String getDefaultUser() {
		return image.getDefaultUser();
	}
	
	public boolean isGenPassword() {
		return image.isGenPassword();
	}
}
