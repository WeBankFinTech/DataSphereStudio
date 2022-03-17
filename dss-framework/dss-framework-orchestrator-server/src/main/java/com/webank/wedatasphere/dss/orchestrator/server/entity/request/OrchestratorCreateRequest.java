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

package com.webank.wedatasphere.dss.orchestrator.server.entity.request;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@XmlRootElement
public class OrchestratorCreateRequest extends OrchestratorRequest {

    @NotNull(message = "编排名称不能为空")
    private String orchestratorName;

    /**
     * 编排模式，如工作流，组合编排等
     */
    @NotNull(message = "编排模式类型不能为空")
    private String orchestratorMode;

    /**
     * 编排方式
     */
    @NotNull(message = "编排方式不能为空")
    private List<String> orchestratorWays;
    private List<String> dssLabels;

    /**
     * 编排用途
     */
    private String uses;

    @NotNull(message = "描述不能为空")
    private String description;

    private String projectName;

    private String workspaceName;

    public List<String> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<String> dssLabels) {
        this.dssLabels = dssLabels;
    }

    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }

    public String getOrchestratorMode() {
        return orchestratorMode;
    }

    public void setOrchestratorMode(String orchestratorMode) {
        this.orchestratorMode = orchestratorMode;
    }

    public List<String> getOrchestratorWays() {
        return orchestratorWays;
    }

    public void setOrchestratorWays(List<String> orchestratorWays) {
        this.orchestratorWays = orchestratorWays;
    }

    public String getUses() {
        return uses;
    }

    public void setUses(String uses) {
        this.uses = uses;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    @Override
    public String toString() {
        return "OrchestratorCreateRequest{" +
                "workspaceId=" + getWorkspaceId() +
                ", projectId=" + getProjectId() +
                ", arrangeName='" + orchestratorName + '\'' +
                ", arrangeMode='" + orchestratorMode + '\'' +
                ", arrangeWays=" + orchestratorWays +
                ", uses='" + uses + '\'' +
                ", description='" + description + '\'' +
                ", labels=" + getLabels() +
                '}';
    }
}
