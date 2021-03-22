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

package com.webank.wedatasphere.dss.standard.app.structure.project.plugin.origin;

import com.webank.wedatasphere.dss.standard.app.structure.project.plugin.ProjectAuth;

import java.util.List;

/**
 * Created by enjoyyin on 2020/8/11.
 */
public class ProjectAuthImpl implements ProjectAuth {

    private String workspaceName;
    private String projectId;
    private String projectName;
    private List<String> editUsers;
    private List<String> accessUsers;
    private List<String> deleteUsers;
    private List<String> executeUsers;

    @Override
    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public List<String> getEditUsers() {
        return editUsers;
    }

    public void setEditUsers(List<String> editUsers) {
        this.editUsers = editUsers;
    }

    @Override
    public List<String> getAccessUsers() {
        return accessUsers;
    }

    @Override
    public List<String> getExecuteUsers() {
        return executeUsers;
    }

    public void setExecuteUsers(List<String> executeUsers) {
        this.executeUsers = executeUsers;
    }

    public void setAccessUsers(List<String> accessUsers) {
        this.accessUsers = accessUsers;
    }

    @Override
    public List<String> getDeleteUsers() {
        if(deleteUsers == null || deleteUsers.isEmpty()) {
            return editUsers;
        }
        return deleteUsers;
    }

    public void setDeleteUsers(List<String> deleteUsers) {
        this.deleteUsers = deleteUsers;
    }

    @Override
    public String toString() {
        return "ProjectAuthImpl(" +
            "workspaceName='" + workspaceName + '\'' +
            ", projectId='" + projectId + '\'' +
            ", projectName='" + projectName + '\'' +
            ", editUsers=" + editUsers +
            ", accessUsers=" + accessUsers +
            ", deleteUsers=" + deleteUsers +
            ')';
    }
}
