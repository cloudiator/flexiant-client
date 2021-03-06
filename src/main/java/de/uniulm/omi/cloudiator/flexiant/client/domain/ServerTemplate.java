/*
 * Copyright (c) 2014-2015 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.flexiant.client.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A class describing a template from which a new server can be started.
 * <p>
 * To create a new instance, use the builder.
 *
 * @see ServerTemplate.FlexiantServerTemplateBuilder
 */
public class ServerTemplate {

    private final String serverName;
    private final String serverProductOffer;
    private final String diskProductOffer;
    private final String vdc;
    private final String image;
    private final TemplateOptions templateOptions;

    private ServerTemplate(final String serverName, final String serverProductOffer,
        final String diskProductOffer, final String vdc, final String image,
        final TemplateOptions templateOptions) {

        checkNotNull(serverName);
        checkArgument(!serverName.isEmpty());
        checkNotNull(serverProductOffer);
        checkArgument(!serverProductOffer.isEmpty());
        checkNotNull(diskProductOffer);
        checkArgument(!diskProductOffer.isEmpty());
        checkNotNull(vdc);
        checkArgument(!vdc.isEmpty());
        checkNotNull(image);
        checkArgument(!image.isEmpty());
        checkNotNull(templateOptions);

        this.serverName = serverName;
        this.serverProductOffer = serverProductOffer;
        this.diskProductOffer = diskProductOffer;
        this.vdc = vdc;
        this.image = image;
        this.templateOptions = templateOptions;

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
     * @return the image that will be used to start the server.
     */
    public String getImage() {
        return image;
    }

    /**
     * @return the template options used for starting the server.
     */
    public TemplateOptions getTemplateOptions() {
        return templateOptions;
    }

    /**
     * Builder for the flexiant server template.
     *
     * @see ServerTemplate
     */
    public static class FlexiantServerTemplateBuilder {

        private String serverName;
        private String serverProductOffer;
        private String diskProductOffer;
        private String vdc;
        private String image;
        private TemplateOptions templateOptions;

        /**
         * No-args constructor.
         * <p>
         * Initializes template options with a default value.
         */
        public FlexiantServerTemplateBuilder() {
            this.templateOptions = new TemplateOptions();
        }

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
         * @see ServerTemplate.FlexiantServerTemplateBuilder#hardware(Hardware)
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
         * @see ServerTemplate.FlexiantServerTemplateBuilder#hardware(Hardware)
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
         * @see ServerTemplate.FlexiantServerTemplateBuilder#serverProductOffer(String)
         * @see ServerTemplate.FlexiantServerTemplateBuilder#diskProductOffer(String)
         */
        public FlexiantServerTemplateBuilder hardware(final Hardware hardware) {
            checkNotNull(hardware);
            return this.hardwareId(hardware.getId());
        }

        /**
         * Uses the given hardware identified by the artificial hardware key.
         *
         * @param hardwareId the id of the hardware object.
         * @return fluent interface
         */
        public FlexiantServerTemplateBuilder hardwareId(final String hardwareId) {
            checkNotNull(hardwareId);
            checkArgument(!hardwareId.isEmpty());
            final String[] ids = hardwareId.split(":");
            checkArgument(ids.length == 2);
            this.serverProductOffer = ids[0];
            this.diskProductOffer = ids[1];
            return this;
        }

        public FlexiantServerTemplateBuilder image(final Image image) {
            this.image = image.getId();
            return this;
        }

        public FlexiantServerTemplateBuilder location(final Location flexiantLocation) {
            checkNotNull(flexiantLocation);
            checkArgument(flexiantLocation.getLocationScope() == LocationScope.VDC);
            this.vdc = flexiantLocation.getId();
            return this;
        }

        public FlexiantServerTemplateBuilder vdc(final String vdc) {
            this.vdc = vdc;
            return this;
        }

        public FlexiantServerTemplateBuilder image(final String image) {
            this.image = image;
            return this;
        }

        public FlexiantServerTemplateBuilder templateOptions(
            final TemplateOptions templateOptions) {
            this.templateOptions = templateOptions;
            return this;
        }

        public ServerTemplate build() {
            return new ServerTemplate(this.serverName, this.serverProductOffer,
                this.diskProductOffer, this.vdc, this.image, this.templateOptions);
        }
    }

}
