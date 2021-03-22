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
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCreateRequestRef;

import java.util.List;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/11/20 10:50
 */
public class WorkflowCreateRequestRef implements OrchestratorCreateRequestRef {

    private String userName;
    private String workflowName;
    private String workspaceName;
    private String contextIDStr;

    private Long parentFlowID=null;

    private DSSOrchestratorInfo dssOrchestratorInfo;
    private  String projectName;
    private  Long projectId;
    private  List<DSSLabel> dssLabelList;


    @Override
    public String getUserName() {
        return userName;
    }


    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    @Override
    public String getWorkspaceName() {
        return workspaceName;
    }

    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    @Override
    public String getContextIDStr() {
        return contextIDStr;
    }

    @Override
    public void setContextIDStr(String contextIDStr) {
        this.contextIDStr = contextIDStr;
    }

    @Override
    public void setDssOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo) {
        this.dssOrchestratorInfo = dssOrchestratorInfo;
    }

    @Override
    public DSSOrchestratorInfo getDSSOrchestratorInfo() {
        return dssOrchestratorInfo;
    }

    public Long getParentFlowID() {
        return parentFlowID;
    }

    public void setParentFlowID(Long parentFlowID) {
        this.parentFlowID = parentFlowID;
    }

    @Override
    public void setDSSLabels(List<DSSLabel> dssLabels) {
        this.dssLabelList = dssLabels;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return dssLabelList;
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

    @Override
    public boolean equals(Object ref) {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }
}
