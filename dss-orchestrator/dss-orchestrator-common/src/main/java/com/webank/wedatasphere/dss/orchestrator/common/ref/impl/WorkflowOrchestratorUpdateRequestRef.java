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

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorUpdateRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.CommonRequestRefImpl;


public class WorkflowOrchestratorUpdateRequestRef extends CommonRequestRefImpl implements OrchestratorUpdateRef {

    private DSSOrchestratorInfo dssOrchestratorInfo;

    @Override
    public void setDescription(String description) {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setUses(String uses) {

    }

    @Override
    public String getUses() {
        return null;
    }

    @Override
    public DSSOrchestratorInfo getOrchestratorInfo() {
        return this.dssOrchestratorInfo;
    }

    @Override
    public void setOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo) {
      this.dssOrchestratorInfo=dssOrchestratorInfo;
    }

    public DSSOrchestratorInfo getDssOrchestratorInfo() {
        return dssOrchestratorInfo;
    }

    public void setDssOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo) {
        this.dssOrchestratorInfo = dssOrchestratorInfo;
    }

    @Override
    public String toString() {
        return "WorkflowOrchestratorUpdateRequestRef{" +
                "userName='" + userName + '\'' +
                ", workspaceName='" + workspaceName + '\'' +
                ", dssOrchestratorInfo=" + dssOrchestratorInfo +
                ", dssLabels=" + dssLabelList +
                '}';
    }
}
