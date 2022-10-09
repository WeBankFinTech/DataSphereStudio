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

import com.webank.wedatasphere.dss.common.entity.DSSWorkspace;
import com.webank.wedatasphere.dss.common.entity.project.Project;
import com.webank.wedatasphere.dss.common.label.DSSLabel;

import java.util.List;
import java.util.Map;


public class RequestConvertOrchestrations {

    private String userName;
    // 这里的 key 为 DSS 具体编排（如 DSS 工作流）的 id，value为SchedulerAppConn的工作流Id
    private Map<Long, Long> orchestrationIdMap;
    private Project project;
    private DSSWorkspace workspace;
    private List<DSSLabel> dssLabels;
    private String approvalId;

    public Map<Long, Long> getOrchestrationIdMap() {
        return orchestrationIdMap;
    }

    public void setOrchestrationIdMap(Map<Long, Long> orchestrationIdMap) {
        this.orchestrationIdMap = orchestrationIdMap;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public DSSWorkspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(DSSWorkspace workspace) {
        this.workspace = workspace;
    }

    public List<DSSLabel> getDSSLabels() {
        return dssLabels;
    }

    public void setDSSLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    public String getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(String approvalId) {
        this.approvalId = approvalId;
    }
}
