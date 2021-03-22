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

package com.webank.wedatasphere.dss.appconn.orchestrator.ref;

import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorDeleteRequestRef;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/12/15 10:52
 */
public class DefaultOrchestratorDeleteRequestRef implements OrchestratorDeleteRequestRef {
    private Long orchestratorId;
    private  Long appId;
    private String userName;
    private String workspaceName;
    private String projectName;

    private List<DSSLabel> dssLabelList;
    @Override
    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    @Override
    public Long getOrchestratorId() {
        return orchestratorId;
    }

    @Override
    public void setAppId(Long appId) {
        this.appId = appId;
    }

    @Override
    public Long getAppId() {
        return this.appId;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getWorkspaceName() {
        return workspaceName;
    }

    @Override
    public void setWorkspaceName(String workspaceName) {
      this.workspaceName = workspaceName;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return this.dssLabelList;
    }

    @Override
    public void setDSSLabels( List<DSSLabel> dssLabelList) {
       this.dssLabelList = dssLabelList;
    }

    @Override
    public Object getParameter(String key) {
        return null;
    }

    @Override
    public void setParameter(String key, Object value) {

    }

    @Override
    public Map<String, Object> getParameters() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }
}
