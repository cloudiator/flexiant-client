package de.uniulm.omi.flexiant;

import de.uniulm.omi.flexiant.extility.ProductOffer;

import static com.google.common.base.Preconditions.checkNotNull;

public class FlexiantHardware {

	private final ProductOffer productOffer;

	public FlexiantHardware(final ProductOffer productOffer) {
		checkNotNull(productOffer);
		this.productOffer = productOffer;
	}
	
	public String getId() {
		return productOffer.getResourceUUID();
	}

}
