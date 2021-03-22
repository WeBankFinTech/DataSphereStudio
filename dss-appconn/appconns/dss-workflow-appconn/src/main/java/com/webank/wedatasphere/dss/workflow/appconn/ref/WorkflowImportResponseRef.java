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

import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorImportResponseRef;

import java.util.HashMap;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/11/24 15:33
 */
public class WorkflowImportResponseRef implements OrchestratorImportResponseRef {

    private JobStatus status;

    private Map<String,Object> responseMap = new HashMap<>();

    private String responseBody;

    private String errorMsg;

    private Long orcRefId;

    private String refOrcContent;

    public JobStatus getResponseStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }




    @Override
    public Object getValue(String key) {
        return responseMap.get(key);
    }

    @Override
    public Map<String, Object> toMap() {
        return this.responseMap;
    }

    @Override
    public String getResponseBody() {
        return this.responseBody;
    }

    @Override
    public int getStatus() {
        return this.status.ordinal();
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public Long getRefOrcId() {
        return this.orcRefId;
    }

    @Override
    public void setRefOrcId(Long refOrcId) {
        this.orcRefId = refOrcId;
    }

    @Override
    public String getRefOrcContent() {
        return this.refOrcContent;
    }

    @Override
    public void setRefOrcContent(String content) {
        this.refOrcContent = content;
    }


}
