/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.workflow.appconn.ref;


import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCreateResponseRef;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/11/20 11:08
 */
public class WorkflowCreateResponseRef implements OrchestratorCreateResponseRef {

    private Long workflowId;

    private String content;



    @Override
    public void setOrchestratorId(Long orcId) {
        this.workflowId = orcId;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public Long getOrchestratorId() {
        return workflowId;
    }



    @Override
    public Long getOrchestratorVersionId() {
        return null;
    }

    @Override
    public void setOrchestratorVersionId(Long versionId) {

    }

    @Override
    public Object getValue(String key) {
        return null;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public String getResponseBody() {
        return null;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getErrorMsg() {
        return null;
    }

    @Override
    public boolean equals(Object ref) {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }
}
