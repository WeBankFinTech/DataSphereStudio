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

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorUpdateRef;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/11/20 11:22
 */
public class WorkflowUpdateRequestRef implements OrchestratorUpdateRef {


   private String userName;
    private Long flowID;
    private String flowName;
    private String description;
    private String uses;

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void setOrcID(Long orcID) {
        this.flowID = orcID;
    }

    @Override
    public Long getOrcID() {
        return null;
    }

    public Long getFlowID() {
        return flowID;
    }



    public String getFlowName() {
        return flowName;
    }


    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getUses() {
        return uses;
    }

    @Override
    public void setUses(String uses) {
        this.uses = uses;
    }

    @Override
    public void setOrchestratorName(String name) {
      this.flowName = name;
    }

    @Override
    public String getOrchestratorName() {
        return null;
    }

    @Override
    public String getWorkspaceName() {
        return null;
    }

    @Override
    public void setWorkspaceName(String workspaceName) {

    }

    @Override
    public String getProjectName() {
        return null;
    }

    @Override
    public void setProjectName(String projectName) {

    }

    @Override
    public DSSOrchestratorInfo getOrchestratorInfo() {
        return null;
    }

    @Override
    public void setOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo) {

    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return null;
    }

    @Override
    public void setDSSLabels(List<DSSLabel> dssLabels) {

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
