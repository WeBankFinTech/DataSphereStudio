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
     * 编排方式
     */
    @NotNull(message = "编排方式不能为空")
    private List<String> orchestratorWays;

    /**
     * 编排重要性级别
     */
    private String orchestratorLevel;

    private List<String> dssLabels;

    /**
     * 编排用途
     */
    private String uses;

    @NotNull(message = "描述不能为空")
    private String description;

    private String projectName;

    private String workspaceName;

    private String isDefaultReference;

    public OrchestratorCreateRequest() {
    }

    public OrchestratorCreateRequest(String orchestratorName, List<String> orchestratorWays, String orchestratorLevel, List<String> dssLabels, String uses, String description, String projectName, String workspaceName) {
        this.orchestratorName = orchestratorName;
        this.orchestratorWays = orchestratorWays;
        this.orchestratorLevel = orchestratorLevel;
        this.dssLabels = dssLabels;
        this.uses = uses;
        this.description = description;
        this.projectName = projectName;
        this.workspaceName = workspaceName;
    }

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

    public String getOrchestratorLevel() {
        return orchestratorLevel;
    }

    public void setOrchestratorLevel(String orchestratorLevel) {
        this.orchestratorLevel = orchestratorLevel;
    }

    public String getIsDefaultReference() {
        return isDefaultReference;
    }

    public void setIsDefaultReference(String isDefaultReference) {
        this.isDefaultReference = isDefaultReference;
    }

    @Override
    public String toString() {
        return "OrchestratorCreateRequest{" +
                "workspaceId=" + getWorkspaceId() +
                ", projectId=" + getProjectId() +
                ", arrangeName='" + orchestratorName + '\'' +
                ", arrangeMode='" + getOrchestratorMode() + '\'' +
                ", arrangeWays=" + orchestratorWays +
                ", uses='" + uses + '\'' +
                ", description='" + description + '\'' +
                ", labels=" + getLabels() +
                '}';
    }
}
