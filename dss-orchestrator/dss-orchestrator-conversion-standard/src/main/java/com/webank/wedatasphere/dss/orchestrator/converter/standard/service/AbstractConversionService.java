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

package com.webank.wedatasphere.dss.orchestrator.converter.standard.service;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ConversionIntegrationStandard;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.operation.ConversionOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.app.AppSingletonIntegrationServiceImpl;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractConversionService extends AppSingletonIntegrationServiceImpl<ConversionOperation, SSORequestService>
    implements ConversionService {

    private List<DSSLabel> dssLabels = new ArrayList<>();
    private ConversionIntegrationStandard appStandard;

    public void setLabels(List<DSSLabel> labels) {
        dssLabels = labels;
    }

    @Override
    public List<DSSLabel> getLabels() {
        return dssLabels;
    }

    @Override
    protected void initOperation(ConversionOperation operation) {
        operation.setConversionService(this);
        operation.init();
    }

    @Override
    public ConversionIntegrationStandard getAppStandard() {
        return appStandard;
    }

    @Override
    public void setAppStandard(ConversionIntegrationStandard appStandard) {
        this.appStandard = appStandard;
    }
}
