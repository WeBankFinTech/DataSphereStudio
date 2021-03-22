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

import com.webank.wedatasphere.dss.common.entity.IOEnv;

/**
 * @author allenlliu
 * @date 2020/12/29 19:57
 */
public class RequestImportWorkflow {
    private String userName;
    private String resourceId;
    private String bmlVersion;
    private Long projectId;
    private String projectName;
    private IOEnv sourceEnv;
    private String orcVersion;
    private String workspaceName;
    private String workspaceStr;

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

    public IOEnv getSourceEnv() {
        return sourceEnv;
    }

    public void setSourceEnv(IOEnv sourceEnv) {
        this.sourceEnv = sourceEnv;
    }

    public String getOrcVersion() {
        return orcVersion;
    }

    public void setOrcVersion(String orcVersion) {
        this.orcVersion = orcVersion;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getWorkspaceStr() {
        return workspaceStr;
    }

    public void setWorkspaceStr(String workspaceStr) {
        this.workspaceStr = workspaceStr;
    }

    public RequestImportWorkflow(String userName,
                                 String resourceId,
                                 String bmlVersion,
                                 Long projectId,
                                 String projectName,
                                 IOEnv sourceEnv,
                                 String orcVersion,
                                 String workspaceName,
                                 String workspaceStr) {
        this.userName = userName;
        this.resourceId = resourceId;
        this.bmlVersion = bmlVersion;
        this.projectId = projectId;
        this.projectName = projectName;
        this.sourceEnv = sourceEnv;
        this.orcVersion = orcVersion;
        this.workspaceName = workspaceName;
        this.workspaceStr = workspaceStr;
    }


}
