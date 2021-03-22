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
public class RequestExportOrchestrator {

    private String userName;
    private String workspaceName;
    private Long orchestratorId;
    private Long orcVersionId;
    private String projectName;
    private List<DSSLabel> dssLabels;
    private Boolean addOrcVersion;
    private String workspaceStr;

    public RequestExportOrchestrator(String userName,
                                     String workspaceName,
                                     Long orchestratorId,
                                     Long orcVersionId,
                                     String projectName,
                                     List<DSSLabel> dssLabels,
                                     Boolean addOrcVersion,
                                     String workspaceStr) {
        this.userName = userName;
        this.workspaceName = workspaceName;
        this.orcVersionId = orcVersionId;
        this.orchestratorId = orchestratorId;
        this.projectName = projectName;
        this.dssLabels = dssLabels;
        this.addOrcVersion = addOrcVersion;
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

    public Long getOrcVersionId() {
        return orcVersionId;
    }

    public void setOrcVersionId(Long orcVersionId) {
        this.orcVersionId = orcVersionId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<DSSLabel> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    public Boolean getAddOrcVersion() {
        return addOrcVersion;
    }

    public void setAddOrcVersion(Boolean addOrcVersion) {
        this.addOrcVersion = addOrcVersion;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getWorkspaceStr() {
        return workspaceStr;
    }

    public void setWorkspaceStr(String workspaceStr) {
        this.workspaceStr = workspaceStr;
    }
}
