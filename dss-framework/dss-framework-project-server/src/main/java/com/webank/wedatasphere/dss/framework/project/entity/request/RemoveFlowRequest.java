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

package com.webank.wedatasphere.dss.framework.project.entity.request;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@XmlRootElement
public class RemoveFlowRequest {

    private Long orchestratorId;

    @NotNull(message = "工程id不能为空")
    private Long projectId;

    @NotNull(message = "工作流名称不能为空")
    private String orchestratorName;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    @Override
    public String toString() {
        return "RemoveFlowRequest{" +
                "orchestratorId=" + orchestratorId +
                ", projectId=" + projectId +
                ", orchestratorName='" + orchestratorName + '\'' +
                '}';
    }
}
