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


public class RequestOrchestratorVersion {

    private String username;
    private Long projectId;
    private Long orchestratorId;



    public static RequestOrchestratorVersion newInstance(String username, Long projectId, Long orchestratorId) {
        RequestOrchestratorVersion requestOrchestratorVersion = new RequestOrchestratorVersion();
        requestOrchestratorVersion.setOrchestratorId(orchestratorId);
        requestOrchestratorVersion.setProjectId(projectId);
        requestOrchestratorVersion.setUsername(username);
        return requestOrchestratorVersion;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }




}
