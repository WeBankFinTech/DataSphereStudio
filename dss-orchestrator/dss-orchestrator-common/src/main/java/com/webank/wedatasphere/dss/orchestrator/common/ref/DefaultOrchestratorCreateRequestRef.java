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

package com.webank.wedatasphere.dss.orchestrator.common.ref;


import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.CommonRequestRefImpl;


public class DefaultOrchestratorCreateRequestRef extends CommonRequestRefImpl implements OrchestratorCreateRequestRef {


    private DSSOrchestratorInfo dssOrchestratorInfo;

    @Override
    public DSSOrchestratorInfo getDSSOrchestratorInfo() {
        return dssOrchestratorInfo;
    }

    @Override
    public void setDssOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo) {
        this.dssOrchestratorInfo = dssOrchestratorInfo;
    }


    @Override
    public String toString() {
        return "WorkflowOrchestratorCreateRequestRef{" +
                "username='" + userName + '\'' +
                ", workspaceName='" + workspaceName + '\'' +
                ", projectName='" + projectName + '\'' +
                ", projectId=" + projectId +
                ", contextIDStr='" + contextID + '\'' +
                ", dssLabels=" + dssLabelList +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
