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

package com.webank.wedatasphere.dss.orchestrator.common.protocol;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;


public class RequestImportOrchestrator {


    private String userName;
    private String projectName;
    private Long projectId;
    private String resourceId;
    private String bmlVersion;
    private String orchestratorName;
    private List<DSSLabel> dssLabels;
    private Workspace workspace;
    private Long copyProjectId;
    private String copyProjectName;

    public RequestImportOrchestrator(String userName,
                                     String projectName,
                                     Long projectId,
                                     String resourceId,
                                     String bmlVersion,
                                     String orchestratorName,
                                     List<DSSLabel> dssLabels,
                                     Workspace workspace) {
        this.userName = userName;
        this.workspace = workspace;
        this.projectName = projectName;
        this.projectId = projectId;
        this.resourceId = resourceId;
        this.bmlVersion = bmlVersion;
        this.dssLabels = dssLabels;
        this.orchestratorName = orchestratorName;
    }

    public RequestImportOrchestrator(String userName,
                                     String projectName,
                                     Long projectId,
                                     String resourceId,
                                     String bmlVersion,
                                     String orchestratorName,
                                     List<DSSLabel> dssLabels,
                                     Workspace workspace,
                                     Long copyProjectId,
                                     String copyProjectName) {
        this.userName = userName;
        this.projectName = projectName;
        this.projectId = projectId;
        this.resourceId = resourceId;
        this.bmlVersion = bmlVersion;
        this.orchestratorName = orchestratorName;
        this.dssLabels = dssLabels;
        this.workspace = workspace;
        this.copyProjectId = copyProjectId;
        this.copyProjectName = copyProjectName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getBmlVersion() {
        return bmlVersion;
    }

    public void setBmlVersion(String bmlVersion) {
        this.bmlVersion = bmlVersion;
    }

    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }

    public List<DSSLabel> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    public Long getCopyProjectId() {
        return copyProjectId;
    }

    public void setCopyProjectId(Long copyProjectId) {
        this.copyProjectId = copyProjectId;
    }

    public String getCopyProjectName() {
        return copyProjectName;
    }

    public void setCopyProjectName(String copyProjectName) {
        this.copyProjectName = copyProjectName;
    }
}
