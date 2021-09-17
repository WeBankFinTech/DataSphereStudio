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

package com.webank.wedatasphere.dss.appconn.workflow.ref;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorUpdateRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.CommonRequestRefImpl;


public class WorkflowUpdateRequestRef extends CommonRequestRefImpl implements OrchestratorUpdateRef {

    private String description;
    private String uses;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getUses() {
        return uses;
    }

    @Override
    public void setUses(String uses) {
        this.uses = uses;
    }

    @Override
    public DSSOrchestratorInfo getOrchestratorInfo() {
        return null;
    }

    @Override
    public void setOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo) {

    }


}
