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

package de.uniulm.omi.flexiant.domain.impl.generic;

import de.uniulm.omi.flexiant.domain.api.ResourceInLocation;
import de.uniulm.omi.flexiant.extility.VirtualResource;

/**
 * Created by daniel on 13.01.15.
 */
public abstract class ResourceInLocationImpl extends ResourceImpl implements ResourceInLocation {

    public ResourceInLocationImpl(VirtualResource virtualResource) {
        super(virtualResource);
    }

    @Override
    public String getLocationUUID() {
        return ((VirtualResource) this.resource).getClusterUUID();
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResourceInLocation
                && this.getId().equals(((ResourceInLocation) obj).getId())
                && this.getLocationUUID().equals(((ResourceInLocation) obj).getLocationUUID());
    }
}
