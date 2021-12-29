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

import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.DSSToRelConversionService;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.RelToOrchestratorConversionService;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.core.AppIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;


public interface ConversionIntegrationStandard extends AppIntegrationStandard<SSORequestService> {

    DSSToRelConversionService getDSSToRelConversionService(AppInstance appInstance);

    RelToOrchestratorConversionService getRelToDSSConversionService(AppInstance appInstance);

    String getAppConnName();

    void setAppConnName(String appConnName);

    @Override
    default String getStandardName() {
        return "conversionIntegrationStandard";
    }

    @Override
    default int getGrade() {
        return 4;
    }

    @Override
    default boolean isNecessary() {
        return false;
    }
}
