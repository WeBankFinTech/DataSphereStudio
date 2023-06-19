/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.common.entity;

import java.io.Serializable;
import java.util.Objects;

public class Resource implements Serializable {
    private String fileName;
    private String resourceId;
    private String version;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Resource resource = (Resource) o;
        return resourceId.equals(resource.resourceId) &&
                version.equals(resource.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId, version);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "fileName='" + fileName + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
