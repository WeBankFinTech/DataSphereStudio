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

package com.webank.wedatasphere.dss.appconn.manager.entity;

import com.webank.wedatasphere.dss.common.entity.Resource;

public class AppConnInfoImpl implements AppConnInfo {

    private String appConnName;
    private String className;
    private Resource appConnResource;

    public void setAppConnName(String appConnName) {
        this.appConnName = appConnName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setAppConnResource(Resource appConnResource) {
        this.appConnResource = appConnResource;
    }

    @Override
    public String getAppConnName() {
        return appConnName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public Resource getAppConnResource() {
        return appConnResource;
    }
}
