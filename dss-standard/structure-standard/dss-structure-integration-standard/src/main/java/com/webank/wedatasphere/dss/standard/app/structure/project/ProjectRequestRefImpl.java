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

package com.webank.wedatasphere.dss.standard.app.structure.project;

import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractRequestRef;

import java.util.List;

public class ProjectRequestRefImpl extends AbstractRequestRef implements ProjectRequestRef {



    private String workspaceName;

    private String createBy;

    private String updateBy;

    private Long id;

    private String name;

    private String description;

    private Workspace workspace;

    private List<String> accessUsers;

    private List<String> editUsers;

    private List<String> releaseUsers;

    @Override
    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getCreateBy() {
        return this.createBy;
    }

    @Override
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public String getUpdateBy() {
        return updateBy;
    }

    @Override
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }


    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public List<String> getAccessUsers() {
        return accessUsers;
    }

    @Override
    public void setAccessUsers(List<String> accessUsers) {
        this.accessUsers = accessUsers;
    }

    @Override
    public List<String> getEditUsers() {
        return editUsers;
    }

    @Override
    public void setEditUsers(List<String> editUsers) {
        this.editUsers = editUsers;
    }

    @Override
    public List<String> getReleaseUsers() {
        return releaseUsers;
    }

    @Override
    public void setReleaseUsers(List<String> releaseUsers) {
        this.releaseUsers = releaseUsers;
    }
}
