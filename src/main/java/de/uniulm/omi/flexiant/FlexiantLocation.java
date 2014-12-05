package de.uniulm.omi.flexiant;

import de.uniulm.omi.flexiant.extility.Cluster;
import de.uniulm.omi.flexiant.extility.Vdc;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class FlexiantLocation extends AbstractFlexiantResource{

	@Nullable
	private final FlexiantLocation parent;

	public static FlexiantLocation from(Cluster cluster) {
		return new FlexiantLocation(cluster);
	}

	public static FlexiantLocation from(Vdc vdc, Cluster cluster) {
		return new FlexiantLocation(vdc, cluster);
	}

	private FlexiantLocation(final Vdc vdc, final Cluster cluster) {
		super(vdc);
		checkNotNull(cluster);
		this.parent = FlexiantLocation.from(cluster);
	}

	private FlexiantLocation(final Cluster cluster) {
		super(cluster);
		this.parent = null;
	}

	@Nullable
	public FlexiantLocation getParent() {
		return this.parent;
	}

}
