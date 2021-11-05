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

package com.webank.wedatasphere.dss.standard.app.development.service;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.operation.DevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.app.AppSingletonIntegrationServiceImpl;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDevelopmentService extends AppSingletonIntegrationServiceImpl<DevelopmentOperation, SSORequestService> implements DevelopmentService {

    private List<DSSLabel> dssLabels = new ArrayList<>();
    private DevelopmentIntegrationStandard appStandard;

    public void setLabels(List<DSSLabel> labels) {
        dssLabels = labels;
    }

    @Override
    public void setAppStandard(DevelopmentIntegrationStandard appStandard) {
        this.appStandard = appStandard;
    }

    @Override
    public DevelopmentIntegrationStandard getAppStandard() {
        return appStandard;
    }

    @Override
    public List<DSSLabel> getLabels() {
        return dssLabels;
    }

    @Override
    protected void initOperation(DevelopmentOperation operation) {
        operation.setDevelopmentService(this);
    }
}
