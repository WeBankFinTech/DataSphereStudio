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

package com.webank.wedatasphere.dss.appconn.visualis.project;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.service.Operation;

public class VisualisProjectService implements ProjectService {

    AppInstance appInstance;
    AppDesc appDesc;
    StructureIntegrationStandard appStandard;
    SSORequestService ssoService;

    @Override
    public boolean isCooperationSupported() {
        return true;
    }

    @Override
    public boolean isProjectNameUnique() {
        return false;
    }

    @Override
    public ProjectCreationOperation createProjectCreationOperation() {
        VisualisProjectCreationOperation visualisProjectCreationOperation = new VisualisProjectCreationOperation(this);
        visualisProjectCreationOperation.setStructureService(this);
        return visualisProjectCreationOperation;
    }

    @Override
    public ProjectUpdateOperation createProjectUpdateOperation() {
        return null;
    }

    @Override
    public ProjectDeletionOperation createProjectDeletionOperation() {
        return null;
    }

    @Override
    public ProjectUrlOperation createProjectUrlOperation() {
        return null;
    }

    @Override
    public AppInstance getAppInstance() {
        return appInstance;
    }

    @Override
    public void setAppInstance(AppInstance appInstance){
        this.appInstance = appInstance;
    }

    @Override
    public void setSSOService(AppIntegrationService ssoService) {
        this.ssoService = (SSORequestService) ssoService;
    }

    @Override
    public AppIntegrationService getSSOService() {
        return ssoService;
    }

    @Override
    public void setAppStandard(StructureIntegrationStandard appStandard) {
        this.appStandard = appStandard;
    }

    @Override
    public StructureIntegrationStandard getAppStandard() {
        return appStandard;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    @Override
    public AppDesc getAppDesc() {
        return appDesc;
    }

    @Override
    public Operation createOperation(Class<? extends Operation> clazz) {
        return null;
    }

    @Override
    public boolean isOperationExists(Class<? extends Operation> clazz) {
        return false;
    }

    @Override
    public boolean isOperationNecessary(Class<? extends Operation> clazz) {
        return false;
    }
}
