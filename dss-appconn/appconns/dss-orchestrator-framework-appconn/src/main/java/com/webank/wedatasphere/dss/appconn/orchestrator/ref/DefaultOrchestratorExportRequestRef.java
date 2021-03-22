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

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/12/15 16:04
 */
public class DefaultOrchestratorExportRequestRef implements OrchestratorExportRequestRef {

    private String userName;
    private Long appId;

    private Long projectId;
    private String projectName;

    private String workspaceName;

    private Long orchestratorVersionId;

    private List<DSSLabel> dssLabels;

    private boolean addOrcVersion;
    private Long orcId;

    private Workspace workspace;


    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setAppId(Long appId) {
        this.appId = appId;
    }

    @Override
    public Long getAppId() {
        return appId;
    }

    @Override
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public Long getProjectId() {
        return projectId;
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
    public void setOrchestratorId(Long orchestratorId) {
        this.orcId = orchestratorId;
    }

    @Override
    public Long getOrchestratorId() {
        return orcId;
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
    public void setOrchestratorVersionId(Long orchestratorVersionId) {
        this.orchestratorVersionId = orchestratorVersionId;
    }


    @Override
    public Long getOrchestratorVersionId() {
        return orchestratorVersionId;
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
    public boolean getAddOrcVersionFlag() {
        return addOrcVersion;
    }

    @Override
    public void setAddOrcVersionFlag(boolean addOrcVersion) {
        this.addOrcVersion = addOrcVersion;
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
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
