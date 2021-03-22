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

package com.webank.wedatasphere.dss.framework.project.entity.request;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * created by cooperyang on 2020/10/16
 * Description:
 */
@XmlRootElement
public class OrchestratorModifyRequest {

    @NotNull(message = "id不能为空")
    private Long id;

    @NotNull(message = "workspaceId不能为空")
    private Long workspaceId;

    @NotNull(message = "工程id不能为空")
    private Long projectId;

    @NotNull(message = "编排名称不能为空")
    private String orchestratorName;

    /**
     * 编排模式，如工作流，组合编排等
     */
    @NotNull(message = "编排模式不能为空")
    private String orchestratorMode;

    /**
     * 编排方式
     */
    @NotNull(message = "编排方式不能为空")
    private List<String> orchestratorWays;

    /**
     * 编排用途
     */
    private String uses;

    @NotNull(message = "描述不能为空")
    private String description;

    /**
     * dssLabels是通过前端进行传入的，主要是用来进行当前的环境信息
     */
    private List<String> dssLabels;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

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

    public List<String> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<String> dssLabels) {
        this.dssLabels = dssLabels;
    }
}
