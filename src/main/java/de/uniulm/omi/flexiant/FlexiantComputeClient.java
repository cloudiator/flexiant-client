/*
 * Copyright 2014 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.flexiant;

import de.uniulm.omi.flexiant.extility.*;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Client for calling compute operations on flexiants extility api.
 */
public class FlexiantComputeClient {

    private final FlexiantBaseClient flexiantBaseClient;

    /**
     * @see de.uniulm.omi.flexiant.FlexiantComputeClient#FlexiantComputeClient(String, String, String)
     */
    public FlexiantComputeClient(final String endpoint, final String apiUserName, final String password) {
        flexiantBaseClient = new FlexiantBaseClient(endpoint, apiUserName, password);
    }

    /**
     * @see FlexiantComputeClient#getService()
     */
    protected UserService getService() {
        return flexiantBaseClient.getService();
    }

    /**
     * @see FlexiantComputeClient#getCustomerUUID()
     */
    protected String getCustomerUUID() {
        return flexiantBaseClient.getCustomerUUID();
    }

    /**
     * Returns all servers whose names are matching the given prefix.
     *
     * @param prefix the prefix the server names should match.
     * @return a list of servers matching the prefix
     * @throws FlexiantException
     */
    public Set<FlexiantServer> getServers(final String prefix) throws FlexiantException {
        Set<FlexiantServer> servers = new HashSet<FlexiantServer>();
        for (Object o : this.getResources(prefix, "resourceName", ResourceType.SERVER)) {
            servers.add(new FlexiantServer((Server) o));
        }
        return servers;
    }

    /**
     * Returns all servers.
     *
     * @return all servers.
     * @throws FlexiantException
     */
    public Set<FlexiantServer> getServers() throws FlexiantException {
        Set<FlexiantServer> servers = new HashSet<FlexiantServer>();
        for (Object o : this.getResources(ResourceType.SERVER)) {
            servers.add(new FlexiantServer((Server) o));
        }
        return servers;
    }

    /**
     * Returns all images.
     *
     * @return all images.
     * @throws FlexiantException
     */
    public Set<FlexiantImage> getImages() throws FlexiantException {
        Set<FlexiantImage> images = new HashSet<FlexiantImage>();
        for (Object o : this.getResources(ResourceType.IMAGE)) {
            images.add(new FlexiantImage((Image) o));
        }
        return images;
    }

    /**
     * Returns all locations.
     *
     * @return all locations.
     * @throws FlexiantException
     */
    public Set<FlexiantLocation> getLocations() throws FlexiantException {
        Set<FlexiantLocation> locations = new HashSet<FlexiantLocation>();
        for (Object o : this.getResources(ResourceType.VDC)) {
            locations.add(FlexiantLocation.from((Vdc) o, (Cluster) this.getResource(((Vdc) o).getClusterUUID(), ResourceType.CLUSTER)));
        }
        for (Object o : this.getResources(ResourceType.CLUSTER)) {
            locations.add(FlexiantLocation.from((Cluster) o));
        }
        return locations;
    }

    /**
     * Retrieves the server having the given ip.
     * <p/>
     * It seems that flexiant does not allow to query by ip. Therefore, this
     * query loops through all servers, finding the ip.
     *
     * @param ip the ip of the server.
     * @return the server having the given ip, or null if not found
     * @throws FlexiantException
     */
    @Nullable
    public FlexiantServer getServerByIp(String ip) throws FlexiantException {
        return this.searchByIp(this.getServers(), ip);
    }

    /**
     * Retrieves the server having the ip and matching the given name filter.
     * <p/>
     * As we need to loop through all existing servers, the name filter can increase
     * the speed.
     *
     * @param ip     the ip of the server.
     * @param filter prefix to filter list of servers
     * @return the server with having the given ip, or null if not found.
     * @throws FlexiantException
     * @see FlexiantComputeClient#getServerByIp(String)
     */
    @Nullable
    public FlexiantServer getServerByIp(String ip, String filter) throws FlexiantException {
        return this.searchByIp(this.getServers(filter), ip);
    }

    /**
     * Searches for the given ip, in the given list of servers.
     *
     * @param servers list of servers to search in.
     * @param ip      ip to search for.
     * @return the server matching the ip or null
     */
    @Nullable
    protected FlexiantServer searchByIp(Set<FlexiantServer> servers, String ip) {

        for (FlexiantServer server : servers) {
            if (server.getPublicIpAddress() != null && server.getPublicIpAddress().equals(ip)) {
                return server;
            }
            if (server.getPrivateIpAddress() != null && server.getPrivateIpAddress().equals(ip)) {
                return server;
            }
        }
        return null;
    }

    /**
     * Creates a server with the given properties.
     *
     * @param serverName         name of the server.
     * @param serverProductOffer the product offer to use.
     * @param diskProductOffer   the product offer to use for the disk.
     * @param vdc                the virtual data center in which the server is created.
     * @param network            the network the node will be attached.
     * @param image              the os image to use for the server.
     * @return the created server.
     * @throws FlexiantException
     */
    public FlexiantServer createServer(String serverName, String serverProductOffer, String diskProductOffer, String vdc, String network, String image) throws FlexiantException {
        Server server = new Server();
        server.setResourceName(serverName);
        server.setCustomerUUID(this.getCustomerUUID());
        server.setProductOfferUUID(serverProductOffer);
        server.setVdcUUID(vdc);
        server.setImageUUID(image);

        Disk disk = new Disk();
        disk.setProductOfferUUID(diskProductOffer);
        disk.setIndex(0);

        server.getDisks().add(disk);

        Nic nic = new Nic();
        nic.setNetworkUUID(network);

        server.getNics().add(nic);

        try {
            Job serverJob = this.getService().createServer(server, null, null);
            this.getService().waitForJob(serverJob.getResourceUUID(), true);
            return this.getServer(serverJob.getItemUUID());
        } catch (ExtilityException e) {
            throw new FlexiantException("Could not create server", e);
        }
    }

    /**
     * Starts the given server.
     *
     * @param serverUUID the uuid of the server
     * @throws FlexiantException
     */
    public void startServer(String serverUUID) throws FlexiantException {
        this.changeServerStatus(serverUUID, ServerStatus.RUNNING);
    }

    /**
     * Starts the given server
     *
     * @param server the server to start.
     * @throws FlexiantException
     * @see de.uniulm.omi.flexiant.FlexiantComputeClient#startServer(String)
     */
    public void startServer(FlexiantServer server) throws FlexiantException {
        if (server == null) {
            throw new IllegalArgumentException("The given server must not be null.");
        }

        this.startServer(server.getId());
    }

    /**
     * Stops the given server.
     *
     * @param serverUUID the uuid of the server to stop.
     * @throws FlexiantException
     */
    public void stopServer(String serverUUID) throws FlexiantException {
        this.changeServerStatus(serverUUID, ServerStatus.STOPPED);
    }

    /**
     * Stops the given server.
     *
     * @param server the server to stop.
     * @throws FlexiantException
     * @see de.uniulm.omi.flexiant.FlexiantComputeClient#stopServer(String)
     */
    public void stopServer(FlexiantServer server) throws FlexiantException {
        if (server == null) {
            throw new IllegalArgumentException("The given server must not be null.");
        }

        this.stopServer(server.getId());
    }

    /**
     * Deletes the given server.
     *
     * @param server the server to be deleted.
     * @throws FlexiantException
     */
    public void deleteServer(final FlexiantServer server) throws FlexiantException {
        this.deleteServer(server.getId());
    }

    /**
     * Deletes the server identified by the given id.
     *
     * @param serverUUID id of the server
     * @throws FlexiantException
     */
    public void deleteServer(final String serverUUID) throws FlexiantException {
        this.deleteResource(serverUUID);
    }

    /**
     * Deletes a resource (and all related entities) identified by the given uuid.
     *
     * @param uuid of the resource.
     * @throws FlexiantException if the resource can not be deleted.
     */
    protected void deleteResource(final String uuid) throws FlexiantException {
        try {
            final Job job = this.getService().deleteResource(uuid, true, null);
            this.getService().waitForJob(job.getResourceUUID(), true);
        } catch (ExtilityException e) {
            throw new FlexiantException("Could not delete resource", e);
        }
    }

    /**
     * Changes the server status to the given status.
     *
     * @param serverUUID the id of the server.
     * @param status     the status the server should change to.
     * @throws FlexiantException
     */
    protected void changeServerStatus(String serverUUID, ServerStatus status) throws FlexiantException {
        try {
            Job job = this.getService().changeServerStatus(serverUUID, status, true, null, null);
            this.getService().waitForJob(job.getResourceUUID(), true);
        } catch (ExtilityException e) {
            throw new FlexiantException("Could not start server", e);
        }
    }

    /**
     * Returns information about the given server.
     *
     * @param serverUUID the id of the server.
     * @return a server object containing the information about the server if any, otherwise null.
     * @throws FlexiantException
     */
    @Nullable
    public FlexiantServer getServer(final String serverUUID) throws FlexiantException {
        final Server server = (Server) this.getResource(serverUUID, ResourceType.SERVER);
        if (server == null) {
            return null;
        }
        return new FlexiantServer(server);
    }

    /**
     * Retrieves the image identified by the given uuid.
     *
     * @param imageUUID the id of the image
     * @return information about the image if any, otherwise null.
     * @throws FlexiantException
     */
    @Nullable
    public FlexiantImage getImage(final String imageUUID) throws FlexiantException {
        final Image image = (Image) this.getResource(imageUUID, ResourceType.IMAGE);
        if (image == null) {
            return null;
        }
        return new FlexiantImage(image);
    }

    /**
     * Retrieves the image identified by the given uuid, which is available at the
     * given location.
     *
     * @param imageUUID    the id of the image.
     * @param locationUUID the id of the location
     * @return an image object if found, null otherwise.
     * @throws FlexiantException
     */
    @Nullable
    public FlexiantImage getImage(final String imageUUID, final String locationUUID) throws FlexiantException {
        final Image image = (Image) this.getResource(imageUUID, locationUUID, ResourceType.IMAGE);
        if (image == null) {
            return null;
        }
        return new FlexiantImage(image);
    }

    /**
     * Retrieves the hardware identified by the given uuid.
     *
     * @param hardwareUUID the id of the hardware
     * @return information about the hardware if any, otherwise null.
     * @throws FlexiantException
     */
    @Nullable
    public FlexiantHardware getHardware(final String hardwareUUID) throws FlexiantException {
        final ProductOffer productOffer = (ProductOffer) this.getResource(hardwareUUID, ResourceType.PRODUCTOFFER);
        if (productOffer == null) {
            return null;
        }
        return new FlexiantHardware(productOffer);
    }

    /**
     * Retrieves the location identified by the given uuid.
     *
     * @param locationUUID the id of the location.
     * @return an object representing the location if any, otherwise null.
     * @throws FlexiantException
     */
    @Nullable
    public FlexiantLocation getLocation(final String locationUUID) throws FlexiantException {

        final Cluster cluster = this.getCluster(locationUUID);
        if (cluster != null) {
            return FlexiantLocation.from(cluster);
        }
        final Vdc vdc = this.getVdc(locationUUID);
        if (vdc != null) {
            return FlexiantLocation.from(vdc, cluster);
        }
        return null;
    }

    /**
     * Loads the cluster with the given uuid.
     *
     * @param clusterUUID the uuid of the cluster.
     * @return the cluster identified by the uuid or null.
     * @throws FlexiantException
     */
    @Nullable
    protected Cluster getCluster(final String clusterUUID) throws FlexiantException {
        return (Cluster) this.getResource(clusterUUID, ResourceType.CLUSTER);
    }

    /**
     * Loads the vdc with the given uuid.
     *
     * @param vdcUUID the uuid of the vdc.
     * @return the vdc identified by the uuid or null.
     * @throws FlexiantException
     */
    @Nullable
    protected Vdc getVdc(final String vdcUUID) throws FlexiantException {
        return (Vdc) this.getResource(vdcUUID, ResourceType.VDC);
    }

    /**
     * Retrieves a ressource from the flexiant api, which is identified by the given resource UUID.
     *
     * @param resourceUUID the uuid of the resource.
     * @param type         the type of the resource.
     * @return an object representing the resource if any, otherwise null.
     * @throws FlexiantException if an error occurred during the call to the api.
     */
    @Nullable
    protected Object getResource(final String resourceUUID, final ResourceType type) throws FlexiantException {

        SearchFilter sf = new SearchFilter();
        FilterCondition fc = new FilterCondition();

        fc.setCondition(Condition.IS_EQUAL_TO);
        fc.setField("resourceUUID");
        fc.getValue().add(resourceUUID);
        sf.getFilterConditions().add(fc);

        return this.getSingleResource(sf, type);
    }

    /**
     * Retrieves a resource from the flexiant api, which is identified by the given resource UUID and the given location UUID.
     * <p/>
     * This method is required if the desired resource is location dependant, and you want to ensure the resource exists at
     * the required location.
     *
     * @param ressourceUUID the uuid of the resource.
     * @param locationUUID  the uuid of the location.
     * @param type          the type of the resource.
     * @return the resource if found, null if not.
     * @throws FlexiantException if multiple resources where found or an error occurred during the call to the api.
     */
    @Nullable
    protected Object getResource(final String ressourceUUID, final String locationUUID, final ResourceType type) throws FlexiantException {
        SearchFilter sf = new SearchFilter();
        FilterCondition fcResource = new FilterCondition();

        fcResource.setCondition(Condition.IS_EQUAL_TO);
        fcResource.setField("resourceUUID");
        fcResource.getValue().add(ressourceUUID);
        sf.getFilterConditions().add(fcResource);

        FilterCondition fcLocation = new FilterCondition();
        fcLocation.setCondition(Condition.IS_EQUAL_TO);
        fcLocation.setField("vdcUUID");
        fcLocation.getValue().add(locationUUID);
        sf.getFilterConditions().add(fcLocation);

        return this.getSingleResource(sf, type);
    }

    /**
     * Retrieves a single resource using the given search filter and the
     * given resource type.
     *
     * @param sf   the search filter.
     * @param type the resource type.
     * @return the resource or null if not found.
     * @throws FlexiantException if multiple resources are returned or an error occurs while contacting the api.
     */
    @Nullable
    protected Object getSingleResource(final SearchFilter sf, final ResourceType type) throws FlexiantException {
        try {
            ListResult result = this.getService().listResources(sf, null, type);

            if (result.getList().size() > 1) {
                throw new FlexiantException("Found multiple resources, single resource expected.");
            }

            if (result.getList().isEmpty()) {
                return null;
            }

            return result.getList().get(0);

        } catch (ExtilityException e) {
            throw new FlexiantException("Error while retrieving resources", e);
        }
    }


    /**
     * Retrieves a list of resources matching the given prefix on the given attribute
     * which are of the given type.
     *
     * @param prefix    the prefix to match.
     * @param attribute the attribute where the prefix should match.
     * @param type      the type of the resource.
     * @return a list containing all resources matching the request.
     * @throws FlexiantException if an error occurs while contacting the api.
     */
    protected List<?> getResources(final String prefix, final String attribute, final ResourceType type) throws FlexiantException {

        SearchFilter sf = new SearchFilter();
        FilterCondition fc = new FilterCondition();

        fc.setCondition(Condition.STARTS_WITH);
        fc.setField(attribute);

        fc.getValue().add(prefix);

        sf.getFilterConditions().add(fc);

        try {
            return this.getService().listResources(sf, null, type).getList();
        } catch (ExtilityException e) {
            throw new FlexiantException(String.format("Error while retrieving resource with prefix %s on attribute %s of type %s", prefix, attribute, type), e);
        }
    }

    /**
     * Returns all resources of the given type.
     *
     * @param type the resource type.
     * @return a list of all resources of the given type.
     * @throws FlexiantException
     */
    protected List<?> getResources(final ResourceType type) throws FlexiantException {
        try {
            return this.getService().listResources(null, null, type).getList();
        } catch (ExtilityException e) {
            throw new FlexiantException(String.format("Error while retrieving resources of type %s.", type), e);
        }
    }
}
