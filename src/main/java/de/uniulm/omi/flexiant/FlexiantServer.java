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

import de.uniulm.omi.flexiant.extility.Ip;
import de.uniulm.omi.flexiant.extility.IpType;
import de.uniulm.omi.flexiant.extility.NetworkType;
import de.uniulm.omi.flexiant.extility.Nic;
import de.uniulm.omi.flexiant.extility.Server;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Wrapper for the flexiant server class.
 *
 * @see de.uniulm.omi.flexiant.extility.Server
 */
public class FlexiantServer {

	private final Server server;
	
	public FlexiantServer(final Server server) {
		checkNotNull(server);
		this.server = server;
	}
	
	public String getId() {
		return server.getResourceUUID();
	}

	@Nullable
    public String getPublicIpAddress() {
    	for (final Nic nic : this.server.getNics()) {
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

    public String getPrivateIpAddress() {
        return this.getPublicIpAddress();
    }

	public String getInitialUser() {
		return this.server.getInitialUser();
	}

	public String getInitialPassword() {
		return this.server.getInitialPassword();
	}
}
