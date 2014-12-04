package de.uniulm.omi.flexiant;

import de.uniulm.omi.flexiant.extility.Cluster;
import de.uniulm.omi.flexiant.extility.Vdc;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class FlexiantLocation {

	@Nullable
	private final Vdc vdc;
	@Nullable
	private final Cluster cluster;
	@Nullable
	private final FlexiantLocation parent;

	public static FlexiantLocation from(Cluster cluster) {
		return new FlexiantLocation(cluster);
	}

	public static FlexiantLocation from(Vdc vdc, Cluster cluster) {
		return new FlexiantLocation(vdc, cluster);
	}

	private FlexiantLocation(final Vdc vdc, final Cluster cluster) {
		checkNotNull(vdc);
		checkNotNull(cluster);
		this.vdc = vdc;
		this.cluster = null;
		this.parent = FlexiantLocation.from(cluster);
	}

	private FlexiantLocation(final Cluster cluster) {
		checkNotNull(cluster);
		this.vdc = null;
		this.cluster = cluster;
		this.parent = null;
	}
	
	public String getId() {
		if(this.vdc != null) {
			return this.vdc.getResourceUUID();
		}
		if(this.cluster != null) {
			return this.cluster.getResourceUUID();
		}
		throw new IllegalStateException("Location was not correctly initialized.");
	}

	@Nullable
	public FlexiantLocation getParent() {
		return this.parent;
	}

}
