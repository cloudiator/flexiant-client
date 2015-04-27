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

import de.uniulm.omi.flexiant.extility.UserAPI;
import de.uniulm.omi.flexiant.extility.UserService;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base client for creating a connection to
 * the flexiant extility api.
 */
public class FlexiantBaseClient {

    private UserService service;
    private String customerUUID;

    /**
     * Constructor for the class.
     * <p>
     * Creates a connection to the server at the given endpoint using
     * the apiUserName and password for auth.
     *
     * @param endpoint    URL for the flexiant api
     * @param apiUserName User for authentication
     * @param password    Password for authentication
     */
    public FlexiantBaseClient(String endpoint, String apiUserName, String password) {

        checkNotNull(endpoint);
        checkNotNull(apiUserName);
        checkNotNull(password);
        checkArgument(!endpoint.isEmpty());
        checkArgument(!apiUserName.isEmpty());
        checkArgument(!password.isEmpty());

        this.customerUUID = apiUserName.split("/")[0];

        // disable sni name checking
        System.setProperty("jsse.enableSNIExtension", "false");

        // Get the service WSDL from the client jar
        URL url;
        try {
            url = new URL(endpoint + "/?wsdl");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String
                .format("The WSDL Url %s is malformed. Check your endpoint.", endpoint + "/?wsdl"),
                e);
        }

        // Get the UserAPI
        UserAPI api = new UserAPI(url, new QName("http://extility.flexiant.net", "UserAPI"));

        // and set the service port on the service
        service = api.getUserServicePort();

        // Get the binding provider
        BindingProvider portBP = (BindingProvider) service;

        // and set the service endpoint
        portBP.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

        // and the caller's authentication details and password
        portBP.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, apiUserName);
        portBP.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
    }

    /**
     * Returns the user service for calling operations on the webservice.
     *
     * @return The service for calling operations on the webservice.
     */
    public UserService getService() {
        return this.service;
    }

    /**
     * Returns the customer uuid of the authenticated user.
     *
     * @return the customer uuid.
     */
    public String getCustomerUUID() {
        return this.customerUUID;
    }

}
