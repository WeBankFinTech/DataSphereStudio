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

package com.webank.wedatasphere.dss.appconn.orchestrator.ref;

import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCreateResponseRef;

import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/12/14 17:37
 */
public class DefaultOrchestratorCreateResponseRef implements OrchestratorCreateResponseRef {

    private Long orcId;
    private String content;
    private String name;
    private Long versionId;

    @Override
    public void setOrchestratorId(Long orcId) {
        this.orcId = orcId;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public Long getOrchestratorId() {
        return orcId;
    }

    @Override
    public Long getOrchestratorVersionId() {
        return versionId;
    }

    @Override
    public void setOrchestratorVersionId(Long versionId) {
       this.versionId = versionId;
    }
    
    @Override
    public void setContent(String content) {
        this.content = content;
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
}
