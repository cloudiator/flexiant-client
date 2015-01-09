package de.uniulm.omi.flexiant.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A class describing a template from which a new server can be started.
 * <p/>
 * To create a new instance, use the builder.
 *
 * @see de.uniulm.omi.flexiant.domain.FlexiantServerTemplate.FlexiantServerTemplateBuilder
 */
public class FlexiantServerTemplate {

    private final String serverName;
    private final String serverProductOffer;
    private final String diskProductOffer;
    private final String vdc;
    private final String network;
    private final String image;

    private FlexiantServerTemplate(
            final String serverName,
            final String serverProductOffer,
            final String diskProductOffer,
            final String vdc,
            final String network,
            final String image) {

        checkNotNull(serverName);
        checkArgument(!serverName.isEmpty());
        checkNotNull(serverProductOffer);
        checkArgument(!serverProductOffer.isEmpty());
        checkNotNull(diskProductOffer);
        checkArgument(!diskProductOffer.isEmpty());
        checkNotNull(vdc);
        checkArgument(!vdc.isEmpty());
        checkNotNull(network);
        checkArgument(!network.isEmpty());
        checkNotNull(image);
        checkArgument(!image.isEmpty());

        this.serverName = serverName;
        this.serverProductOffer = serverProductOffer;
        this.diskProductOffer = diskProductOffer;
        this.vdc = vdc;
        this.network = network;
        this.image = image;
    }

    /**
     * @return the name for the server
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @return the id of the product offer for the server.
     */
    public String getServerProductOffer() {
        return serverProductOffer;
    }

    /**
     * @return the id of the product offer used for the server's disk.
     */
    public String getDiskProductOffer() {
        return diskProductOffer;
    }

    /**
     * @return the vdc in which the server will be placed.
     */
    public String getVdc() {
        return vdc;
    }

    /**
     * @return the network the server will be attached to.
     */
    public String getNetwork() {
        return network;
    }

    /**
     * @return the image that will be used to start the server.
     */
    public String getImage() {
        return image;
    }

    /**
     * Builder for the flexiant server template.
     *
     * @see de.uniulm.omi.flexiant.domain.FlexiantServerTemplate
     */
    public static class FlexiantServerTemplateBuilder {

        private String serverName;
        private String serverProductOffer;
        private String diskProductOffer;
        private String vdc;
        private String network;
        private String image;

        /**
         * Sets the name of the server.
         *
         * @param serverName the name for the server.
         * @return fluent interface
         */
        public FlexiantServerTemplateBuilder serverName(final String serverName) {
            this.serverName = serverName;
            return this;
        }

        /**
         * Sets the product offer for the server.
         *
         * @param serverProductOffer id of the server product offer.
         * @return fluent interface
         * @see de.uniulm.omi.flexiant.domain.FlexiantServerTemplate.FlexiantServerTemplateBuilder#hardware(FlexiantHardware)
         */
        public FlexiantServerTemplateBuilder serverProductOffer(final String serverProductOffer) {
            this.serverProductOffer = serverProductOffer;
            return this;
        }

        /**
         * Sets the product offer for the disk of the server.
         *
         * @param diskProductOffer id of the disk product offer.
         * @return fluent interface
         * @see de.uniulm.omi.flexiant.domain.FlexiantServerTemplate.FlexiantServerTemplateBuilder#hardware(FlexiantHardware)
         */
        public FlexiantServerTemplateBuilder diskProductOffer(final String diskProductOffer) {
            this.diskProductOffer = diskProductOffer;
            return this;
        }

        /**
         * Uses the given hardware to set the server and disk offer.
         *
         * @param hardware a hardware object used for server and disk offer.
         * @return fluent interface
         * @see de.uniulm.omi.flexiant.domain.FlexiantServerTemplate.FlexiantServerTemplateBuilder#serverProductOffer(String)
         * @see de.uniulm.omi.flexiant.domain.FlexiantServerTemplate.FlexiantServerTemplateBuilder#diskProductOffer(String)
         */
        public FlexiantServerTemplateBuilder hardware(final FlexiantHardware hardware) {
            checkNotNull(hardware);
            final String[] ids = hardware.getId().split(":");
            checkArgument(ids.length == 2);
            this.serverProductOffer = ids[0];
            this.diskProductOffer = ids[1];
            return this;
        }

        public FlexiantServerTemplateBuilder image(final FlexiantImage image) {
            this.image = image.getId();
            return this;
        }

        public FlexiantServerTemplateBuilder location(final FlexiantLocation flexiantLocation) {
            checkNotNull(flexiantLocation);
            checkArgument(flexiantLocation.getLocationScope() == FlexiantLocationScope.VDC);
            this.vdc = flexiantLocation.getId();
            return this;
        }

        public FlexiantServerTemplateBuilder vdc(final String vdc) {
            this.vdc = vdc;
            return this;
        }

        public FlexiantServerTemplateBuilder network(final String network) {
            this.network = network;
            return this;
        }

        public FlexiantServerTemplateBuilder image(final String image) {
            this.image = image;
            return this;
        }

        public FlexiantServerTemplate build() {
            return new FlexiantServerTemplate(
                    this.serverName,
                    this.serverProductOffer,
                    this.diskProductOffer,
                    this.vdc,
                    this.network,
                    this.image);
        }
    }

}
