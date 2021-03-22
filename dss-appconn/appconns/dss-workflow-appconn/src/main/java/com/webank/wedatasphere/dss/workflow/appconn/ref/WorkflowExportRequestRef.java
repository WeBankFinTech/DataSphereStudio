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

import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorExportRequestRef;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/11/19 17:52
 */
public class WorkflowExportRequestRef implements OrchestratorExportRequestRef {

    private String userName;
    private Long flowId;
    private  Long projectId;
    private  String projectName;
    private Workspace workspace;

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void setAppId(Long appId) {
        this.flowId = appId;
    }

    @Override
    public Long getAppId() {
        return null;
    }

    public Long getFlowId() {
        return flowId;
    }



    @Override
    public Long getProjectId() {
        return projectId;
    }

    @Override
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public void setOrchestratorId(Long orchestratorId) {

    }

    @Override
    public Long getOrchestratorId() {
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
    public void setOrchestratorVersionId(Long orchestratorVersionId) {

    }

    @Override
    public Long getOrchestratorVersionId() {
        return null;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return null;
    }

    @Override
    public void setDSSLabels(List<DSSLabel> dssLabels) {

    }

    @Override
    public boolean getAddOrcVersionFlag() {
        return false;
    }

    @Override
    public void setAddOrcVersionFlag(boolean addOrcVersion) {

    }

    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
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
