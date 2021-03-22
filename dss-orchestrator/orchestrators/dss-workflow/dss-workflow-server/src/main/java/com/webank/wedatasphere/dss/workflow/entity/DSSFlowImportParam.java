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

package com.webank.wedatasphere.dss.workflow.entity;

import com.webank.wedatasphere.dss.common.entity.IOEnv;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;


/**
 * @author allenlliu
 * @date 2020/11/02 08:21 PM
 */
public class DSSFlowImportParam {

    String workspaceName;
    Long projectID;
    String projectName;
    String version;
    String orcVersion;
    IOEnv sourceEnv;
    Workspace workspace;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    String userName;

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public Long getProjectID() {
        return projectID;
    }

    public void setProjectID(Long projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOrcVersion() {
        return orcVersion;
    }

    public void setOrcVersion(String orcVersion) {
        this.orcVersion = orcVersion;
    }

    public IOEnv getSourceEnv() {
        return sourceEnv;
    }

    public void setSourceEnv(IOEnv sourceEnv) {
        this.sourceEnv = sourceEnv;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
