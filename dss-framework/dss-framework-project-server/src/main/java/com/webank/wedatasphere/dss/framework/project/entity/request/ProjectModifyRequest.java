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

import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectDataSource;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@XmlRootElement
public class ProjectModifyRequest {


    @NotNull(message = "工程名称不能为空")
    private String name;

    @NotNull(message = "工程id不能为空")
    private Long id;

    @NotNull(message = "应用领域不能为空")
    private String applicationArea;

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    private String workspaceName;

    //业务
    private String business;

    //编辑权限用户
    private List<String> editUsers;

    //查看权限用户
    private List<String> accessUsers;

    //发布权限用户
    private List<String> releaseUsers;

    @NotNull(message = "工程描述不能为空")
    private String description;

    //产品
    private String product;

    /**
     * 工作空间名,因为是全局唯一的
     */
    @NotNull(message = "workspaceId不能为空")
    private Long workspaceId;

    /*
   开发流程 code list
    */
    private List<String> devProcessList;

    /**
     * 编排模式 code list
     */
    private List<String> orchestratorModeList;
    private List<DSSProjectDataSource> dataSourceList;

    /**
     * 1-接入git 0-不接入（默认）
     */
    private Boolean associateGit;

    /**
     * 接入git的读写用户名及token
     */
    private String gitUser;
    private String gitToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationArea() {
        return applicationArea;
    }

    public void setApplicationArea(String applicationArea) {
        this.applicationArea = applicationArea;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
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

    public List<String> getReleaseUsers() {
        return releaseUsers;
    }

    public void setReleaseUsers(List<String> releaseUsers) {
        this.releaseUsers = releaseUsers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public List<String> getDevProcessList() {
        return devProcessList;
    }

    public void setDevProcessList(List<String> devProcessList) {
        this.devProcessList = devProcessList;
    }

    public List<String> getOrchestratorModeList() {
        return orchestratorModeList;
    }

    public void setOrchestratorModeList(List<String> orchestratorModeList) {
        this.orchestratorModeList = orchestratorModeList;
    }


    public List<DSSProjectDataSource> getDataSourceList() {
        return dataSourceList;
    }

    public void setDataSourceList(List<DSSProjectDataSource> dataSourceList) {
        this.dataSourceList = dataSourceList;
    }

    @Override
    public String toString() {
        return "ProjectModifyRequest{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", applicationArea='" + applicationArea + '\'' +
                ", business='" + business + '\'' +
                ", editUsers=" + editUsers +
                ", accessUsers=" + accessUsers +
                ", releaseUsers=" + releaseUsers +
                ", description='" + description + '\'' +
                ", product='" + product + '\'' +
                ", workspaceId=" + workspaceId +
                ", devProcessList=" + devProcessList +
                ", orchestratorModeList=" + orchestratorModeList +
                ", associateGit=" + associateGit +
                ", gitUser=" + gitUser +
                ", gitToken=" + gitToken +
                '}';
    }

    public Boolean getAssociateGit() {
        return associateGit;
    }

    public void setAssociateGit(Boolean associateGit) {
        this.associateGit = associateGit;
    }

    public String getGitUser() {
        return gitUser;
    }

    public void setGitUser(String gitUser) {
        this.gitUser = gitUser;
    }

    public String getGitToken() {
        return gitToken;
    }

    public void setGitToken(String gitToken) {
        this.gitToken = gitToken;
    }
}
