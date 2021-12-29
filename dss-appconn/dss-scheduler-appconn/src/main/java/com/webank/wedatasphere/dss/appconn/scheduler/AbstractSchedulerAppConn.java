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

package com.webank.wedatasphere.dss.appconn.scheduler;

import com.webank.wedatasphere.dss.appconn.core.impl.AbstractOnlySSOAppConn;
import com.webank.wedatasphere.dss.workflow.conversion.WorkflowConversionIntegrationStandard;

public abstract class AbstractSchedulerAppConn extends AbstractOnlySSOAppConn implements SchedulerAppConn {

    private WorkflowConversionIntegrationStandard workflowConversionIntegrationStandard;

    @Override
    protected void initialize() {
        workflowConversionIntegrationStandard = new WorkflowConversionIntegrationStandard();
    }

    @Override
    public WorkflowConversionIntegrationStandard getOrCreateWorkflowConversionStandard() {
        workflowConversionIntegrationStandard.setSSORequestService(this.getOrCreateSSOStandard().getSSORequestService());
        workflowConversionIntegrationStandard.setAppConnName(getAppDesc().getAppName());
        return workflowConversionIntegrationStandard;
    }
}
