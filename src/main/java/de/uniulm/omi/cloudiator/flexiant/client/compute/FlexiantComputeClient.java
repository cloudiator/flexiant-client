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

package de.uniulm.omi.cloudiator.flexiant.client.compute;

import de.uniulm.omi.cloudiator.flexiant.client.api.FlexiantException;
import de.uniulm.omi.cloudiator.flexiant.client.domain.Hardware;
import de.uniulm.omi.cloudiator.flexiant.client.domain.Location;
import de.uniulm.omi.cloudiator.flexiant.client.domain.ServerTemplate;
import io.github.cloudiator.flexiant.extility.*;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Client for calling compute operations on flexiants extility api.
 */
public class FlexiantComputeClient {

    private final FlexiantBaseClient flexiantBaseClient;

    /**
     * @see FlexiantComputeClient#FlexiantComputeClient(String, String, String)
     */
    public FlexiantComputeClient(final String endpoint, final String apiUserName,
        final String password) {
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
     * @param prefix       the prefix the server names should match.
     * @param locationUUID optional location of the server, if null it will be ignored
     * @return a list of servers matching the prefix
     * @throws de.uniulm.omi.cloudiator.flexiant.client.api.FlexiantException
     */
    public Set<de.uniulm.omi.cloudiator.flexiant.client.domain.Server> getServers(
        final String prefix, @Nullable String locationUUID) throws FlexiantException {
        return this
            .getResources(prefix, "resourceName", ResourceType.SERVER, Server.class, locationUUID)
            .stream()
            .map(o -> new de.uniulm.omi.cloudiator.flexiant.client.domain.Server((Server) o))
            .collect(Collectors.toSet());
    }

    /**
     * Returns all servers.
     *
     * @param locationUUID optional location of the server, if null it will be ignored.
     * @return all servers.
     * @throws FlexiantException
     */
    public Set<de.uniulm.omi.cloudiator.flexiant.client.domain.Server> getServers(
        @Nullable final String locationUUID) throws FlexiantException {
        return this.getResources(ResourceType.SERVER, Server.class, locationUUID).stream()
            .map(de.uniulm.omi.cloudiator.flexiant.client.domain.Server::new)
            .collect(Collectors.toSet());
    }

    /**
     * Returns all images.
     *
     * @param locationUUID optional location of the image, if null it will be ignored.
     * @return all images.
     * @throws FlexiantException
     */
    public Set<de.uniulm.omi.cloudiator.flexiant.client.domain.Image> getImages(
        @Nullable final String locationUUID) throws FlexiantException {
        return this.getResources(ResourceType.IMAGE, Image.class, locationUUID).stream()
            .map(de.uniulm.omi.cloudiator.flexiant.client.domain.Image::new)
            .collect(Collectors.toSet());
    }

    /**
     * Returns all locations.
     *
     * @return all locations.
     * @throws FlexiantException
     */
    public Set<Location> getLocations() throws FlexiantException {
        Set<Location> locations = new HashSet<Location>();
        for (Vdc vdc : this.getResources(ResourceType.VDC, Vdc.class, null)) {
            locations.add(Location.from(vdc,
                this.getResource((vdc).getClusterUUID(), ResourceType.CLUSTER, Cluster.class)));
        }
        locations.addAll(this.getResources(ResourceType.CLUSTER, Cluster.class, null).stream()
            .map(Location::from).collect(Collectors.toList()));
        return locations;
    }

    /**
     * Returns all hardware.
     *
     * @param locationUUID optional location of the hardware flavor, if null it will be ignored.
     * @return all hardware.
     * @throws FlexiantException
     */
    public Set<Hardware> getHardwareFlavors(@Nullable final String locationUUID)
        throws FlexiantException {
        //noinspection unchecked
        return Hardware
            .from(this.getResources(ResourceType.PRODUCTOFFER, ProductOffer.class, locationUUID),
                this.getResources(ResourceType.CLUSTER, Cluster.class, null));
    }

    /**
     * Returns all networks.
     *
     * @param locationUUID optional location of the network, if null it will be ignored.
     * @return a set of all networks.
     * @throws FlexiantException
     */
    public Set<de.uniulm.omi.cloudiator.flexiant.client.domain.Network> getNetworks(
        @Nullable final String locationUUID) throws FlexiantException {

        return this.getResources(ResourceType.NETWORK, Network.class, locationUUID).stream()
            .map(de.uniulm.omi.cloudiator.flexiant.client.domain.Network::new)
            .collect(Collectors.toSet());
    }

    /**
     * Retrieves the server having the given ip.
     * <p/>
     * It seems that flexiant does not allow to query by ip. Therefore, this
     * query loops through all servers, finding the ip.
     *
     * @param ip           the ip of the server.
     * @param locationUUID optional location of the server, if null it will be ignored.
     * @return the server having the given ip, or null if not found
     * @throws FlexiantException
     */
    @Nullable public de.uniulm.omi.cloudiator.flexiant.client.domain.Server getServerByIp(String ip,
        @Nullable String locationUUID) throws FlexiantException {
        return this.searchByIp(this.getServers(locationUUID), ip);
    }

    /**
     * Retrieves the server having the ip and matching the given name filter.
     * <p/>
     * As we need to loop through all existing servers, the name filter can increase
     * the speed.
     *
     * @param ip           the ip of the server.
     * @param filter       prefix to filter list of servers
     * @param locationUUID optional location of the server, if null it will be ignored.
     * @return the server with having the given ip, or null if not found.
     * @throws FlexiantException
     */
    @Nullable public de.uniulm.omi.cloudiator.flexiant.client.domain.Server getServerByIp(String ip,
        String filter, @Nullable String locationUUID) throws FlexiantException {
        return this.searchByIp(this.getServers(filter, locationUUID), ip);
    }

    /**
     * Searches for the given ip, in the given list of servers.
     *
     * @param servers list of servers to search in.
     * @param ip      ip to search for.
     * @return the server matching the ip or null
     */
    @Nullable protected de.uniulm.omi.cloudiator.flexiant.client.domain.Server searchByIp(
        Set<de.uniulm.omi.cloudiator.flexiant.client.domain.Server> servers, String ip) {
        for (de.uniulm.omi.cloudiator.flexiant.client.domain.Server server : servers) {
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
     * @param serverTemplate A template describing the server which should be started.
     * @return the created server.
     * @throws FlexiantException
     */
    public de.uniulm.omi.cloudiator.flexiant.client.domain.Server createServer(
        final ServerTemplate serverTemplate) throws FlexiantException {

        checkNotNull(serverTemplate);

        io.github.cloudiator.flexiant.extility.Server server =
            new io.github.cloudiator.flexiant.extility.Server();
        server.setResourceName(serverTemplate.getServerName());
        server.setCustomerUUID(this.getCustomerUUID());
        server.setProductOfferUUID(serverTemplate.getServerProductOffer());
        server.setVdcUUID(serverTemplate.getVdc());
        server.setImageUUID(serverTemplate.getImage());

        Disk disk = new Disk();
        disk.setProductOfferUUID(serverTemplate.getDiskProductOffer());
        disk.setIndex(0);
        server.getDisks().add(disk);

        final Set<String> networks = new HashSet<String>();
        if (serverTemplate.getTemplateOptions().getNetworks().isEmpty()) {
            //no network configured, find it out by ourselves.
            if (this.getNetworks(serverTemplate.getVdc()).size() == 1) {
                networks.add(this.getNetworks(serverTemplate.getVdc()).iterator().next().getId());
            } else {
                throw new FlexiantException("Could not uniquely identify network.");
            }
        } else {
            networks.addAll(serverTemplate.getTemplateOptions().getNetworks());
        }

        //assign the networks
        for (String network : networks) {
            Nic nic = new Nic();
            nic.setNetworkUUID(network);
            server.getNics().add(nic);
        }

        try {
            Job serverJob = this.getService().createServer(server, null, null, null);
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
     * @see FlexiantComputeClient#startServer(String)
     */
    public void startServer(de.uniulm.omi.cloudiator.flexiant.client.domain.Server server)
        throws FlexiantException {
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
     * @see FlexiantComputeClient#stopServer(String)
     */
    public void stopServer(de.uniulm.omi.cloudiator.flexiant.client.domain.Server server)
        throws FlexiantException {
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
    public void deleteServer(final de.uniulm.omi.cloudiator.flexiant.client.domain.Server server)
        throws FlexiantException {
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
    protected void changeServerStatus(String serverUUID, ServerStatus status)
        throws FlexiantException {
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
    @Nullable public de.uniulm.omi.cloudiator.flexiant.client.domain.Server getServer(
        final String serverUUID) throws FlexiantException {
        final io.github.cloudiator.flexiant.extility.Server server =
            this.getResource(serverUUID, ResourceType.SERVER,
                io.github.cloudiator.flexiant.extility.Server.class);
        if (server == null) {
            return null;
        }
        return new de.uniulm.omi.cloudiator.flexiant.client.domain.Server(server);
    }

    /**
     * Retrieves the image identified by the given uuid.
     *
     * @param imageUUID the id of the image
     * @return information about the image if any, otherwise null.
     * @throws FlexiantException
     */
    @Nullable public de.uniulm.omi.cloudiator.flexiant.client.domain.Image getImage(
        final String imageUUID) throws FlexiantException {
        final io.github.cloudiator.flexiant.extility.Image image =
            this.getResource(imageUUID, ResourceType.IMAGE,
                io.github.cloudiator.flexiant.extility.Image.class);
        if (image == null) {
            return null;
        }
        return new de.uniulm.omi.cloudiator.flexiant.client.domain.Image(image);
    }

    /**
     * Retrieves the hardware identified by the given uuid.
     *
     * @param hardwareUUID the id of the hardware
     * @param locationUUID the id of the location
     * @return information about the hardware if any, otherwise null.
     * @throws FlexiantException
     */
    @Nullable public Hardware getHardware(final String hardwareUUID, final String locationUUID)
        throws FlexiantException {

        checkNotNull(hardwareUUID);

        String[] parts = hardwareUUID.split(":");

        checkArgument(parts.length == 2, "Expected hardwareUUID to contain :");

        final ProductOffer machineOffer =
            this.getResource(parts[0], ResourceType.PRODUCTOFFER, ProductOffer.class);
        final ProductOffer diskOffer =
            this.getResource(parts[1], ResourceType.PRODUCTOFFER, ProductOffer.class);


        if (machineOffer == null || diskOffer == null) {
            return null;
        }
        return Hardware.from(machineOffer, diskOffer, locationUUID);
    }

    /**
     * Retrieves the location identified by the given uuid.
     *
     * @param locationUUID the id of the location.
     * @return an object representing the location if any, otherwise null.
     * @throws FlexiantException
     */
    @Nullable public Location getLocation(final String locationUUID) throws FlexiantException {

        final Cluster cluster = this.getCluster(locationUUID);
        if (cluster != null) {
            return Location.from(cluster);
        }
        final Vdc vdc = this.getVdc(locationUUID);
        if (vdc != null) {
            return Location.from(vdc, this.getCluster(vdc.getClusterUUID()));
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
    @Nullable protected Cluster getCluster(final String clusterUUID) throws FlexiantException {
        return this.getResource(clusterUUID, ResourceType.CLUSTER, Cluster.class);
    }

    /**
     * Loads the vdc with the given uuid.
     *
     * @param vdcUUID the uuid of the vdc.
     * @return the vdc identified by the uuid or null.
     * @throws FlexiantException
     */
    @Nullable protected Vdc getVdc(final String vdcUUID) throws FlexiantException {
        return this.getResource(vdcUUID, ResourceType.VDC, Vdc.class);
    }

    /**
     * Retrieves a resource
     *
     * @param resourceUUID the uuid of the resource
     * @param resourceType the type of the resource
     * @param type         the type of the resulting class
     * @param <T>          the type of the resulting class
     * @return the retrieved resource or null if not found
     * @throws FlexiantException
     */
    @Nullable protected <T> T getResource(final String resourceUUID,
        final ResourceType resourceType, final Class<T> type) throws FlexiantException {

        SearchFilter sf = new SearchFilter();
        FilterCondition fc = new FilterCondition();

        fc.setCondition(Condition.IS_EQUAL_TO);
        fc.setField("resourceUUID");
        fc.getValue().add(resourceUUID);
        sf.getFilterConditions().add(fc);

        //noinspection unchecked
        return this.getSingleResource(sf, resourceType, type);
    }

    /**
     * Retrieves a single resource using the given search filter and the
     * given resource type.
     *
     * @param sf           the search filter.
     * @param resourceType the resource type.
     * @param type         the type of the resulting class.
     * @return the resource or null if not found.
     * @throws FlexiantException if multiple resources are returned or an error occurs while contacting the api.
     */
    @Nullable protected <T> T getSingleResource(final SearchFilter sf,
        final ResourceType resourceType, final Class<T> type) throws FlexiantException {
        try {
            ListResult result = this.getService().listResources(sf, null, resourceType);

            if (result.getList().size() > 1) {
                throw new FlexiantException("Found multiple resources, single resource expected.");
            }

            if (result.getList().isEmpty()) {
                return null;
            }

            //noinspection unchecked
            return (T) result.getList().get(0);

        } catch (ExtilityException e) {
            throw new FlexiantException("Error while retrieving resources", e);
        }
    }


    /**
     * Retrieves a list of resources matching the given prefix on the given attribute
     * which are of the given type.
     *
     * @param prefix       the prefix to match.
     * @param attribute    the attribute where the prefix should match.
     * @param resourceType the type of the resource.
     * @param type         the type of the resulting class.
     * @param locationUUID optional location of the type, if null it will be ignored.
     * @return a list containing all resources matching the request.
     * @throws FlexiantException if an error occurs while contacting the api.
     */
    protected <T> List<T> getResources(final String prefix, final String attribute,
        final ResourceType resourceType, final Class<T> type, @Nullable final String locationUUID)
        throws FlexiantException {

        SearchFilter sf = new SearchFilter();
        FilterCondition fc = new FilterCondition();

        fc.setCondition(Condition.STARTS_WITH);
        fc.setField(attribute);

        fc.getValue().add(prefix);

        sf.getFilterConditions().add(fc);

        if (locationUUID != null) {
            FilterCondition fcLocation = new FilterCondition();
            fcLocation.setCondition(Condition.IS_EQUAL_TO);
            fcLocation.setField("vdcUUID");
            fcLocation.getValue().add(locationUUID);
            sf.getFilterConditions().add(fcLocation);
        }

        try {
            //noinspection unchecked
            return (List<T>) this.getService().listResources(sf, null, resourceType).getList();
        } catch (ExtilityException e) {
            throw new FlexiantException(String.format(
                "Error while retrieving resource with prefix %s on attribute %s of resourceType %s",
                prefix, attribute, resourceType), e);
        }
    }

    /**
     * Returns all resources of the given type.
     *
     * @param resourceType the resource type.
     * @param locationUUID optional location of the type, if null it will be ignored
     * @param type         the type of the resulting class.
     * @return a list of all resources of the given type.
     * @throws FlexiantException
     */
    protected <T> List<T> getResources(final ResourceType resourceType, final Class<T> type,
        @Nullable final String locationUUID) throws FlexiantException {

        SearchFilter sf = new SearchFilter();

        if (locationUUID != null) {
            FilterCondition fcLocation = new FilterCondition();
            fcLocation.setCondition(Condition.IS_EQUAL_TO);
            fcLocation.setField("vdcUUID");
            fcLocation.getValue().add(locationUUID);
            sf.getFilterConditions().add(fcLocation);
        }

        try {
            //noinspection unchecked
            return (List<T>) this.getService().listResources(sf, null, resourceType).getList();
        } catch (ExtilityException e) {
            throw new FlexiantException(
                String.format("Error while retrieving resources of resourceType %s.", resourceType),
                e);
        }
    }
}
