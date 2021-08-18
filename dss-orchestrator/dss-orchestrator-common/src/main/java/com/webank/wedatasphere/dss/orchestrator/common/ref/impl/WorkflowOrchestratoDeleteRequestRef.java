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

package com.webank.wedatasphere.dss.orchestrator.common.ref.impl;


import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorDeleteRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.CommonRequestRefImpl;

public class WorkflowOrchestratoDeleteRequestRef extends CommonRequestRefImpl implements OrchestratorDeleteRequestRef {

    private Long orchestratorId;

    @Override
    public Long getOrcId() {
        return this.orchestratorId;
    }

    @Override
    public void setAppId(Long appId) {

    }

    @Override
    public Long getAppId() {
        return null;
    }

    @Override
    public void setOrcId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    @Override
    public String toString() {
        return "WorkflowOrchestratoDeleteRequestRef{" +
                "userName='" + userName + '\'' +
                ", workspaceName='" + workspaceName + '\'' +
                ", projectName='" + projectName + '\'' +
                ", orchestratorId=" + orchestratorId +
                ", dssLabels=" + dssLabelList +
                '}';
    }
}
