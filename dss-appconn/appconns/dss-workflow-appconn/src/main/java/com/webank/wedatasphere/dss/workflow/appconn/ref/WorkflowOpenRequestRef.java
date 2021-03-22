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

package com.webank.wedatasphere.dss.workflow.appconn.ref;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorOpenRequestRef;

import java.util.List;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/12/23 15:24
 */
public class WorkflowOpenRequestRef  implements OrchestratorOpenRequestRef {

    private Long orchestratorId;
    private String userName;
    private Long appId;
    private String secondaryType;
    private List<DSSLabel> dssLabels;
    @Override
    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    @Override
    public Long getOrchestratorId() {
        return this.orchestratorId;
    }

    @Override
    public void setUserName(String userName) {
     this.userName = userName;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setRefAppId(Long appId) {
        this.appId = appId;
    }

    @Override
    public Long getRefAppId() {
        return this.appId;
    }

    @Override
    public String getSecondaryType() {
        return this.secondaryType;
    }

    @Override
    public void setSecondaryType(String secondaryType) {
      this.secondaryType = secondaryType;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return dssLabels;
    }

    @Override
    public void setDSSLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
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
