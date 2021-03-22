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

package com.webank.wedatasphere.dss.framework.project.entity.vo;

import java.util.List;

/**
 * created by cooperyang on 2020/10/16
 * Description:
 */
public class DSSFlowOrchestratorFlowVo {

    private Integer orchestratorId;

    private String orchestratorVersion;

    /**
     * flowId 传递到前端让前端使用进行工作流的渲染
     */
    private String flowId;

    private String description;

    private String name;

    private List<String> tags;

    public Integer getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Integer orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getOrchestratorVersion() {
        return orchestratorVersion;
    }

    public void setOrchestratorVersion(String orchestratorVersion) {
        this.orchestratorVersion = orchestratorVersion;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
