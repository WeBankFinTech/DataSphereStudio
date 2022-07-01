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

package com.webank.wedatasphere.dss.standard.app.structure;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.role.RoleService;
import com.webank.wedatasphere.dss.standard.app.structure.status.AppStatusService;
import com.webank.wedatasphere.dss.standard.common.core.AbstractAppIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

public abstract class AbstractStructureIntegrationStandard extends AbstractAppIntegrationStandard<StructureService, SSORequestService>
    implements StructureIntegrationStandard {

    protected abstract ProjectService createProjectService();

    protected RoleService createRoleService() {
        return null;
    }

    protected AppStatusService createAppStatusService() {
        return null;
    }

    @Override
    protected <T extends StructureService> void initService(T service) {
        service.setAppStandard(this);
    }

    @Override
    public void init()  {
    }

    @Override
    public final RoleService getRoleService(AppInstance appInstance) {
        return getOrCreate(appInstance, this::createRoleService, RoleService.class);
    }

    @Override
    public final ProjectService getProjectService(AppInstance appInstance) {
        return getOrCreate(appInstance, this::createProjectService, ProjectService.class);
    }

    @Override
    public final AppStatusService getAppStateService(AppInstance appInstance) {
        return getOrCreate(appInstance, this::createAppStatusService, AppStatusService.class);
    }

    @Override
    public void close() {
    }

}
