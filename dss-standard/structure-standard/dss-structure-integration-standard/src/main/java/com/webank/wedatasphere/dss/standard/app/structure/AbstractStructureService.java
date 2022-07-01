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
import com.webank.wedatasphere.dss.standard.common.app.AppSingletonIntegrationServiceImpl;

public class AbstractStructureService extends AppSingletonIntegrationServiceImpl<StructureOperation, SSORequestService> implements StructureService {

    private StructureIntegrationStandard appStandard;

    @Override
    protected void initOperation(StructureOperation operation) {
        operation.setStructureService(this);
        super.initOperation(operation);
    }

    @Override
    public void setAppStandard(StructureIntegrationStandard appStandard) {
        this.appStandard = appStandard;
    }

    @Override
    public StructureIntegrationStandard getAppStandard() {
        return appStandard;
    }

}
