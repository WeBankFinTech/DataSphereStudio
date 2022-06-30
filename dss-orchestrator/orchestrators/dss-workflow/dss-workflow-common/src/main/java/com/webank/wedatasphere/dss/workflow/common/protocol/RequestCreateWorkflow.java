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

package com.webank.wedatasphere.dss.workflow.common.protocol;

import com.webank.wedatasphere.dss.common.label.DSSLabel;

import java.util.List;


public class RequestCreateWorkflow {

    private String userName;
    private Long projectId;
    private String workflowName;
    private String contextIDStr;
    private  String description;
    private  Long parentFlowID;
    private  String  uses;
    private List<String > linkedAppConnNames;
    private List<DSSLabel> dssLabels;
    private String orcVersion;
    private String schedulerAppConnName;

    public RequestCreateWorkflow(String userName,
                                 Long projectId,
                                 String workflowName,
                                 String contextIDStr,
                                 String description,
                                 Long parentFlowID,
                                 String uses,
                                 List<String> linkedAppConnNames,
                                 List<DSSLabel> dssLabels,
                                 String orcVersion,
                                 String schedulerAppConnName) {
        this.userName = userName;
        this.projectId = projectId;
        this.workflowName = workflowName;
        this.contextIDStr = contextIDStr;
        this.description = description;
        this.parentFlowID = parentFlowID;
        this.uses = uses;
        this.linkedAppConnNames = linkedAppConnNames;
        this.dssLabels =dssLabels;
        this.orcVersion = orcVersion;
        this.schedulerAppConnName = schedulerAppConnName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getContextIDStr() {
        return contextIDStr;
    }

    public void setContextIDStr(String contextIDStr) {
        this.contextIDStr = contextIDStr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getParentFlowID() {
        return parentFlowID;
    }

    public void setParentFlowID(Long parentFlowID) {
        this.parentFlowID = parentFlowID;
    }

    public String getUses() {
        return uses;
    }

    public void setUses(String uses) {
        this.uses = uses;
    }

    public List<String> getLinkedAppConnNames() {
        return linkedAppConnNames;
    }

    public void setLinkedAppConnNames(List<String> linkedAppConnNames) {
        this.linkedAppConnNames = linkedAppConnNames;
    }
    public List<DSSLabel> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    public String getOrcVersion() {
        return orcVersion;
    }

    public void setOrcVersion(String orcVersion) {
        this.orcVersion = orcVersion;
    }

    public String getSchedulerAppConnName() {
        return schedulerAppConnName;
    }

    public void setSchedulerAppConnName(String schedulerAppConnName) {
        this.schedulerAppConnName = schedulerAppConnName;
    }
}
