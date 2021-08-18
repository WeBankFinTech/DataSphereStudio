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

package com.webank.wedatasphere.dss.orchestrator.converter.standard;

import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.ConversionService;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.DSSToRelConversionService;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.RelToOrchestratorConversionService;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.core.AbstractAppIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import java.io.IOException;


public abstract class AbstractConversionIntegrationStandard extends AbstractAppIntegrationStandard<ConversionService, SSORequestService>
    implements ConversionIntegrationStandard {

    private String appConnName;

    protected abstract DSSToRelConversionService createDSSToRelConversionService();

    protected abstract RelToOrchestratorConversionService createRelToDSSConversionService();

    @Override
    public DSSToRelConversionService getDSSToRelConversionService(AppInstance appInstance) {
        return getOrCreate(appInstance, this::createDSSToRelConversionService, DSSToRelConversionService.class);
    }

    @Override
    public RelToOrchestratorConversionService getRelToDSSConversionService(AppInstance appInstance) {
        return getOrCreate(appInstance, this::createRelToDSSConversionService, RelToOrchestratorConversionService.class);
    }

    @Override
    protected <T extends ConversionService> void initService(T service) {
        super.initService(service);
        service.setAppStandard(this);
    }

    @Override
    public void init() throws AppStandardErrorException {

    }

    @Override
    public String getAppConnName() {
        return appConnName;
    }

    @Override
    public void setAppConnName(String appConnName) {
        this.appConnName = appConnName;
    }

    @Override
    public void close() throws IOException {

    }
}
