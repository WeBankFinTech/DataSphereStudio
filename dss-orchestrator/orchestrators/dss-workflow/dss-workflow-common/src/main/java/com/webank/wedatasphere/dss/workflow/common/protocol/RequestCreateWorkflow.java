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

package com.webank.wedatasphere.dss.workflow.common.protocol;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/12/29 20:12
 */
public class RequestCreateWorkflow {

    private String userName;
    private String workflowName;
    private String contextIDStr;
    private  String description;
    private  Long parentFlowID;
    private  String  uses;
    private List<String > linkedAppConnNames;
    private List<DSSLabel> dssLabels;

    public RequestCreateWorkflow(String userName,
                                 String workflowName,
                                 String contextIDStr,
                                 String description,
                                 Long parentFlowID,
                                 String uses,
                                 List<String> linkedAppConnNames,
                                 List<DSSLabel> dssLabels) {
        this.userName = userName;
        this.workflowName = workflowName;
        this.contextIDStr = contextIDStr;
        this.description = description;
        this.parentFlowID = parentFlowID;
        this.uses = uses;
        this.linkedAppConnNames = linkedAppConnNames;
        this.dssLabels =dssLabels;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
