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

package com.webank.wedatasphere.dss.orchestrator.common.protocol;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;

/**
 * created by cooperyang on 2020/12/28
 * Description:
 */
public class RequestImportOrchestrator {


    private String userName;
    private String workspaceName;
    private String projectName;
    private Long projectId;
    private String resourceId;
    private String bmlVersion;
    private String orchestratorName;
    private List<DSSLabel> dssLabels;
    private String workspaceStr;

    public RequestImportOrchestrator(String userName,
                                     String workspaceName,
                                     String projectName,
                                     Long projectId,
                                     String resourceId,
                                     String bmlVersion,
                                     String orchestratorName,
                                     List<DSSLabel> dssLabels,
                                     String workspaceStr) {
        this.userName = userName;
        this.workspaceName = workspaceName;
        this.projectName = projectName;
        this.projectId = projectId;
        this.resourceId = resourceId;
        this.bmlVersion = bmlVersion;
        this.dssLabels = dssLabels;
        this.orchestratorName = orchestratorName;
        this.workspaceStr = workspaceStr;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
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

    public String getWorkspaceStr() {
        return workspaceStr;
    }

    public void setWorkspaceStr(String workspaceStr) {
        this.workspaceStr = workspaceStr;
    }
}
