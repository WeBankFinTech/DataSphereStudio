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
 * @author allenlliu
 * @date 2020/12/29 20:09
 */
public class RequestDeleteOrchestrator {
   private String userName;
   private String  workspaceName;
   private String   projectName;
   private Long orchestratorId;
   private List<DSSLabel> dssLabels;


    public RequestDeleteOrchestrator(String userName,
                                     String workspaceName,
                                     String projectName,
                                     Long orchestratorId,
                                     List<DSSLabel> dssLabels) {
        this.userName = userName;
        this.workspaceName = workspaceName;
        this.projectName = projectName;
        this.orchestratorId = orchestratorId;
        this.dssLabels = dssLabels;
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

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public List<DSSLabel> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }




}
