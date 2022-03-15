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

public class RequestImportWorkflow {
    private String userName;
    private String resourceId;
    private String bmlVersion;
    private Long projectId;
    private String projectName;
    private String orcVersion;
    private String workspaceStr;
    private String contextId;
    private List<DSSLabel> dssLabels;

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getBmlVersion() {
        return bmlVersion;
    }

    public void setBmlVersion(String bmlVersion) {
        this.bmlVersion = bmlVersion;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public String getWorkspaceStr() {
        return workspaceStr;
    }

    public void setWorkspaceStr(String workspaceStr) {
        this.workspaceStr = workspaceStr;
    }

    public List<DSSLabel> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    public RequestImportWorkflow(String userName,
                                 String resourceId,
                                 String bmlVersion,
                                 Long projectId,
                                 String projectName,
                                 String orcVersion,
                                 String workspaceStr,
                                 String contextId,
                                 List<DSSLabel> dssLabels) {
        this.userName = userName;
        this.resourceId = resourceId;
        this.bmlVersion = bmlVersion;
        this.projectId = projectId;
        this.projectName = projectName;
        this.orcVersion = orcVersion;
        this.workspaceStr = workspaceStr;
        this.contextId = contextId;
        this.dssLabels = dssLabels;
    }


}
