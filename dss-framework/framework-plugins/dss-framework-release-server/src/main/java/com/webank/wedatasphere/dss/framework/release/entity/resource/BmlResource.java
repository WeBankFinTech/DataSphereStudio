/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.release.entity.resource;

/**
 * created by cooperyang on 2020/12/10
 * Description:
 */
public class BmlResource {

    private String resourceId;

    private String version;




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

    public static BmlResource newResource(String resourceId, String version){
        BmlResource bmlResource = new BmlResource();
        bmlResource.setResourceId(resourceId);
        bmlResource.setVersion(version);
        return bmlResource;
    }


    @Override
    public String toString() {
        return "BmlResource{" +
                "resourceId='" + resourceId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
