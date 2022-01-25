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

package com.webank.wedatasphere.dss.workflow.conversion;

import com.webank.wedatasphere.dss.orchestrator.converter.standard.AbstractConversionIntegrationStandard;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.DSSToRelConversionService;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.RelToOrchestratorConversionService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefQueryService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.workflow.conversion.service.WorkflowToRelConversionService;


public class WorkflowConversionIntegrationStandard extends AbstractConversionIntegrationStandard {

    @Override
    protected DSSToRelConversionService createDSSToRelConversionService() {
        return new WorkflowToRelConversionService();
    }

    @Override
    protected RelToOrchestratorConversionService createRelToDSSConversionService() {
        return null;
    }

    public RefQueryService getQueryService(AppInstance appInstance) {
        return null;
    }
}
