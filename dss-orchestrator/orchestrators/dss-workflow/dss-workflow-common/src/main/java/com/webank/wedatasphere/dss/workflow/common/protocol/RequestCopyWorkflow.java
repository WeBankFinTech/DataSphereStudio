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
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;

public class RequestCopyWorkflow {
   private String  userName;
   private Workspace  workspace;
   private Long  rootFlowId;
   private String  contextIdStr;
   private String projectName;
   private String  orcVersion;
   private String  description;
   private List<DSSLabel> dssLabels;

   private Long targetProjectId;
   private String nodeSuffix;
   private String newFlowName;

    public RequestCopyWorkflow(String userName,
                               Workspace workspace,
                               Long rootFlowId,
                               String contextIdStr,
                               String projectName,
                               String orcVersion,
                               String description,
                               List<DSSLabel> dssLabels,
                               Long targetProjectId,
                               String nodeSuffix,
                               String newFlowName) {

        this.userName = userName;
        this.workspace = workspace;
        this.rootFlowId = rootFlowId;
        this.contextIdStr = contextIdStr;
        this.projectName = projectName;
        this.orcVersion = orcVersion;
        this.description = description;
        this.dssLabels = dssLabels;
        this.targetProjectId = targetProjectId;
        this.nodeSuffix = nodeSuffix;
        this.newFlowName = newFlowName;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public Long getRootFlowId() {
        return rootFlowId;
    }

    public void setRootFlowId(Long  rootFlowId) {
        this.rootFlowId = rootFlowId;
    }

    public String getContextIdStr() {
        return contextIdStr;
    }

    public void setContextIdStr(String contextIdStr) {
        this.contextIdStr = contextIdStr;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getOrcVersion() {
        return orcVersion;
    }

    public void setOrcVersion(String orcVersion) {
        this.orcVersion = orcVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DSSLabel> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    public Long getTargetProjectId() {
        return targetProjectId;
    }

    public void setTargetProjectId(Long targetProjectId) {
        this.targetProjectId = targetProjectId;
    }

    public String getNodeSuffix() {
        return nodeSuffix;
    }

    public void setNodeSuffix(String nodeSuffix) {
        this.nodeSuffix = nodeSuffix;
    }

    public String getNewFlowName() {
        return newFlowName;
    }

    public void setNewFlowName(String newFlowName) {
        this.newFlowName = newFlowName;
    }
}
