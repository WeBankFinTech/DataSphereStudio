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

package com.webank.wedatasphere.dss.framework.project.entity.request;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;


@XmlRootElement
public class ProjectQueryRequest implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    @NotNull(message = "workspaceId不能为空")
    private Long workspaceId;

    private String username;

    private boolean filterProject;
    /**
     * 标签
     */
    private String creatorLabel;

    /**
     * 项目名称列表
     * **/
    private List<String> projectNames;


    /**
     * 项目创建人
     * **/
    private  List<String> createUsers;

    /**
     * 发布权限用户
     * **/
    private List<String> releaseUsers;

    /**
     * 编辑权限用户
     * **/
    private List<String> editUsers;

    /**
     * 查看权限用户
     * **/
    private List<String> accessUsers;

    private Integer pageNow;

    private Integer pageSize;


    public Integer getPageNow() {
        return pageNow;
    }

    public void setPageNow(Integer pageNow) {
        this.pageNow = pageNow;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public boolean getFilterProject() {
        return filterProject;
    }

    public void setFilterProject(boolean filterProject) {
        this.filterProject = filterProject;
    }

    public String getCreatorLabel() {
        return creatorLabel;
    }

    public void setCreatorLabel(String creatorLabel) {
        this.creatorLabel = creatorLabel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isFilterProject() {
        return filterProject;
    }

    public List<String> getProjectNames() {
        return projectNames;
    }

    public void setProjectNames(List<String> projectNames) {
        this.projectNames = projectNames;
    }

    public List<String> getCreateUsers() {
        return createUsers;
    }

    public void setCreateUsers(List<String> createUsers) {
        this.createUsers = createUsers;
    }

    public List<String> getReleaseUsers() {
        return releaseUsers;
    }

    public void setReleaseUsers(List<String> releaseUsers) {
        this.releaseUsers = releaseUsers;
    }

    public List<String> getEditUsers() {
        return editUsers;
    }

    public void setEditUsers(List<String> editUsers) {
        this.editUsers = editUsers;
    }

    public List<String> getAccessUsers() {
        return accessUsers;
    }

    public void setAccessUsers(List<String> accessUsers) {
        this.accessUsers = accessUsers;
    }
}
