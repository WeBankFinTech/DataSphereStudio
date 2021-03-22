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

package com.webank.wedatasphere.dss.orchestrator.core.ref;



import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by cooperyang on 2020/12/14
 * Description: 此类是用于从projectFrameWork发到orchestrator框架,进行创建工作流的编排模式
 */
public class WorkflowOrchestratorCreateRequestRef implements OrchestratorCreateRequestRef {


    private String username;

    private String workspaceName;

    private String projectName;

    private Long projectId;

    private DSSOrchestratorInfo dssOrchestratorInfo;

    private String contextIDStr;

    private List<DSSLabel> dssLabels;

    private Map<String, Object> params = new HashMap<>();

    private String name;

    private String type;

    @Override
    public void setDSSLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return this.dssLabels;
    }

    @Override
    public Object getParameter(String key) {
        return this.params.get(key);
    }

    @Override
    public void setParameter(String key, Object value) {
        this.params.put(key, value);
    }

    @Override
    public Map<String, Object> getParameters() {
        return this.params;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getUserName() {
        return username;
    }

    @Override
    public void setUserName(String username) {
        this.username = username;
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
    public Long getProjectId() {
        return projectId;
    }

    @Override
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public DSSOrchestratorInfo getDSSOrchestratorInfo() {
        return dssOrchestratorInfo;
    }

    @Override
    public void setDssOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo) {
        this.dssOrchestratorInfo = dssOrchestratorInfo;
    }

    @Override
    public String getContextIDStr() {
        return contextIDStr;
    }

    @Override
    public void setContextIDStr(String contextIDStr) {
        this.contextIDStr = contextIDStr;
    }

    public List<DSSLabel> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    @Override
    public String toString() {
        return "WorkflowOrchestratorCreateRequestRef{" +
                "username='" + username + '\'' +
                ", workspaceName='" + workspaceName + '\'' +
                ", projectName='" + projectName + '\'' +
                ", projectId=" + projectId +
                ", contextIDStr='" + contextIDStr + '\'' +
                ", dssLabels=" + dssLabels +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
