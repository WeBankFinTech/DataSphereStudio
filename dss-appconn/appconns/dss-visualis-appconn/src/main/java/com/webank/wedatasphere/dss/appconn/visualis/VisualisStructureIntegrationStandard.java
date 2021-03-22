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

package com.webank.wedatasphere.dss.appconn.visualis;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.visualis.project.VisualisProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.role.RoleService;
import com.webank.wedatasphere.dss.standard.app.structure.status.AppStatusService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class VisualisStructureIntegrationStandard implements StructureIntegrationStandard {
    private static final Logger LOGGER = LoggerFactory.getLogger(VisualisDevelopmentIntegrationStandard.class);
    private AppConn appConn;
    public VisualisStructureIntegrationStandard(AppConn appConn){
        try {
            this.appConn = appConn;
            init();
        } catch (AppStandardErrorException e) {
            LOGGER.error("Failed to init {}", this.getClass().getSimpleName(), e);
        }
    }

    @Override
    public RoleService getRoleService() {
        return null;
    }

    @Override
    public ProjectService getProjectService() {
        VisualisProjectService visualisProjectService = new VisualisProjectService();
        visualisProjectService.setAppDesc(getAppDesc());
        visualisProjectService.setAppStandard(this);
        return visualisProjectService;
    }

    @Override
    public AppStatusService getAppStateService() {
        return null;
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appConn.getAppDesc();
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        //do nothing
    }

    @Override
    public void init() throws AppStandardErrorException {

    }

    @Override
    public void close() throws IOException {

    }
}
