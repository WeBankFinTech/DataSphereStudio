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


import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorProdDetail;

import java.util.List;

/**
 * created by cooperyang on 2021/1/22
 * Description:
 */
public class ResponseProdOrcDetail {

    private Long projectId;

    private String dssLabel;

    private String username;

    private List<OrchestratorProdDetail> orchestratorProdDetails;


    public ResponseProdOrcDetail(Long projectId, String dssLabel, String username, List<OrchestratorProdDetail> orchestratorProdDetails) {
        this.projectId = projectId;
        this.dssLabel = dssLabel;
        this.username = username;
        this.orchestratorProdDetails = orchestratorProdDetails;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getDssLabel() {
        return dssLabel;
    }

    public void setDssLabel(String dssLabel) {
        this.dssLabel = dssLabel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<OrchestratorProdDetail> getOrchestratorProdDetails() {
        return orchestratorProdDetails;
    }

    public void setOrchestratorProdDetails(List<OrchestratorProdDetail> orchestratorProdDetails) {
        this.orchestratorProdDetails = orchestratorProdDetails;
    }
}
