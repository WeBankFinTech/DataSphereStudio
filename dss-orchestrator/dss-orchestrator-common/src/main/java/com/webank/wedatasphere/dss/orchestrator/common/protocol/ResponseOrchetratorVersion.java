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


import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;

import java.util.List;


public class ResponseOrchetratorVersion {

    private Long projectId;
    private Long orchestratorId;
    private List<DSSOrchestratorVersion> orchestratorVersions;

    public ResponseOrchetratorVersion(Long projectId, Long orchestratorId, List<DSSOrchestratorVersion> orchestratorVersions) {
        this.projectId = projectId;
        this.orchestratorId = orchestratorId;
        this.orchestratorVersions = orchestratorVersions;
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

    public List<DSSOrchestratorVersion> getOrchestratorVersions() {
        return orchestratorVersions;
    }

    public void setOrchestratorVersions(List<DSSOrchestratorVersion> orchestratorVersions) {
        this.orchestratorVersions = orchestratorVersions;
    }
}
