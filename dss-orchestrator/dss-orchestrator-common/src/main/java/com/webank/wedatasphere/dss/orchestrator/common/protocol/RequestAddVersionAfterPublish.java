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

import java.io.Serializable;
import java.util.List;

public class RequestAddVersionAfterPublish implements Serializable {

    private String userName;
    private Workspace workspace;
    private Long orchestratorId;
    private Long orcVersionId;
    private String projectName;
    private List<DSSLabel> dssLabels;
    //发布备注
    private String comment;

    public RequestAddVersionAfterPublish(String userName,
                                         Workspace workspace,
                                         Long orchestratorId,
                                         Long orcVersionId,
                                         String projectName,
                                         List<DSSLabel> dssLabels, String comment) {
        this.userName = userName;
        this.workspace = workspace;
        this.orcVersionId = orcVersionId;
        this.orchestratorId = orchestratorId;
        this.projectName = projectName;
        this.dssLabels = dssLabels;
        this.comment = comment;
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

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
