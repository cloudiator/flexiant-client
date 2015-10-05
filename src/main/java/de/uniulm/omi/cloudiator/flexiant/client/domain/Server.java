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

import de.uniulm.omi.cloudiator.flexiant.client.domain.generic.ResourceInLocationImpl;
import io.github.cloudiator.flexiant.extility.Ip;
import io.github.cloudiator.flexiant.extility.IpType;
import io.github.cloudiator.flexiant.extility.NetworkType;
import io.github.cloudiator.flexiant.extility.Nic;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Wrapper for the flexiant server class.
 *
 * @see io.github.cloudiator.flexiant.extility.Server
 */
public class Server extends ResourceInLocationImpl {

    public Server(final io.github.cloudiator.flexiant.extility.Server server) {
        super(server);
        checkNotNull(server);
    }

    protected io.github.cloudiator.flexiant.extility.Server getServer() {
        return (io.github.cloudiator.flexiant.extility.Server) this.resource;
    }

    @Nullable public String getPublicIpAddress() {
        for (final Nic nic : this.getServer().getNics()) {
            if (nic.getNetworkType().equals(NetworkType.IP)) {
                for (final Ip ip : nic.getIpAddresses()) {
                    if (ip.getType().equals(IpType.IPV_4)) {
                        return ip.getIpAddress();
                    }
                }
            }
        }
        return null;
    }

    @Nullable public String getPrivateIpAddress() {
        return this.getPublicIpAddress();
    }

    @Nullable public String getInitialUser() {
        return this.getServer().getInitialUser();
    }

    @Nullable public String getInitialPassword() {
        return this.getServer().getInitialPassword();
    }
}
