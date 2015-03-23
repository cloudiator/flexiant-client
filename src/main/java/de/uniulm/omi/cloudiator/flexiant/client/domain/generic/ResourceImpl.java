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

package de.uniulm.omi.cloudiator.flexiant.client.domain.generic;

import de.uniulm.omi.cloudiator.flexiant.client.api.Resource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 05.12.14.
 */
public abstract class ResourceImpl implements Resource {

    protected final de.uniulm.omi.flexiant.extility.Resource resource;

    public ResourceImpl(de.uniulm.omi.flexiant.extility.Resource resource) {
        checkNotNull(resource);
        this.resource = resource;
    }

    @Override
    public String getId() {
        return this.resource.getResourceUUID();
    }

    @Override
    public String getName() {
        return this.resource.getResourceName();
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Resource && this.getId().equals(((Resource) obj).getId());
    }

    @Override
    public String toString() {
        return String.format("FlexiantResource{uuid=%s}", this.getId());
    }
}
