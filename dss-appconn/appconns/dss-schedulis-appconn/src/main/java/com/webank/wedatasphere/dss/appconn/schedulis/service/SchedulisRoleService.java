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

package com.webank.wedatasphere.dss.appconn.schedulis.service;

import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.role.*;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.service.Operation;

/**
 * created by cooperyang on 2020/11/12
 * Description:
 */
public class SchedulisRoleService implements RoleService {
    @Override
    public RoleCreationOperation createRoleCreationOperation() {
        return null;
    }

    @Override
    public RoleUpdateOperation createRoleUpdateOperation() {
        return null;
    }

    @Override
    public RoleDeletionOperation createRoleDeletionOperation() {
        return null;
    }

    @Override
    public RoleUrlOperation createRoleUrlOperation() {
        return null;
    }

    @Override
    public void setSSOService(AppIntegrationService ssoService) {

    }

    @Override
    public AppIntegrationService getSSOService() {
        return null;
    }

    @Override
    public void setAppStandard(StructureIntegrationStandard appStandard) {

    }

    @Override
    public StructureIntegrationStandard getAppStandard() {
        return null;
    }

    @Override
    public AppInstance getAppInstance() {
        return null;
    }

    @Override
    public void setAppInstance(AppInstance appInstance) {

    }

    @Override
    public void setAppDesc(AppDesc appDesc) {

    }

    @Override
    public AppDesc getAppDesc() {
        return null;
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
