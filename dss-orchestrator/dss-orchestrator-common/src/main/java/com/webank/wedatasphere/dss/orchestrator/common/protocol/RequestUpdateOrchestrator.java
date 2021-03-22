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
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/12/29 17:12
 */
public class RequestUpdateOrchestrator {
    private String userName;
    private String workspaceName;
    private DSSOrchestratorInfo dssOrchestratorInfo;
    private List<DSSLabel> dssLabels;


    public RequestUpdateOrchestrator(String userName,
                                     String workspaceName,
                                     DSSOrchestratorInfo dssOrchestratorInfo,
                                     List<DSSLabel> dssLabels) {
        this.userName = userName;
        this.workspaceName = workspaceName;
        this.dssOrchestratorInfo = dssOrchestratorInfo;
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

    public DSSOrchestratorInfo getDssOrchestratorInfo() {
        return dssOrchestratorInfo;
    }

    public void setDssOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo) {
        this.dssOrchestratorInfo = dssOrchestratorInfo;
    }

    public List<DSSLabel> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }


}
