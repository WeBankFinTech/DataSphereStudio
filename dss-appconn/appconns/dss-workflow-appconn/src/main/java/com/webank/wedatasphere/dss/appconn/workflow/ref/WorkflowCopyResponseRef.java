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

import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorCopyResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.CommonResponseRef;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;


public class WorkflowCopyResponseRef extends CommonResponseRef implements OrchestratorCopyResponseRef{

    private DSSFlow dssFlow;

    private Long copyOrcId;

    private Long copyTargetAppId;

    private String copyTargetContent;

    public DSSFlow getDssFlow() {
        return dssFlow;
    }

    public void setDssFlow(DSSFlow dssFlow) {
        this.dssFlow = dssFlow;
    }

    @Override
    public long getCopyOrcId() {
        return copyOrcId;
    }

    public void setCopyOrcId(Long copyOrcId) {
        this.copyOrcId = copyOrcId;
    }

    @Override
    public long getCopyTargetAppId() {
        return copyTargetAppId;
    }

    public void setCopyTargetAppId(Long copyTargetAppId) {
        this.copyTargetAppId = copyTargetAppId;
    }

    @Override
    public String getCopyTargetContent() {
        return copyTargetContent;
    }

    public void setCopyTargetContent(String copyTargetContent) {
        this.copyTargetContent = copyTargetContent;
    }
}
