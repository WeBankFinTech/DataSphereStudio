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
public class ProjectCreateRequest {

    @NotNull(message = "工程名称不能为空")
    private String name;

    @NotNull(message = "应用领域不能为空")
    private Integer applicationArea;

    private String business;

    //产品
    private String product;

    private List<String> releaseUsers;

    private List<String> editUsers;

    private List<String> accessUsers;

    @NotNull(message = "工程描述不能为空")
    private String description;

    /**
     * 工作空间名,因为是全局唯一的
     */
    @NotNull(message = "workspaceId不能为空")
    private Long workspaceId;

    private String workspaceName;
    /**
     * 开发流程 list
     */
    private List<String> devProcessList;

    /**
     * 编码模式 list
     */
    private List<String> orchestratorModeList;
    /**
     * 项目上的数据源列表
     */
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

    /**
     * 标签
     */
    private String creatorLabel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getApplicationArea() {
        return applicationArea;
    }

    public void setApplicationArea(Integer applicationArea) {
        this.applicationArea = applicationArea;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
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
        return "ProjectCreateRequest{" +
                "name='" + name + '\'' +
                ", applicationArea=" + applicationArea +
                ", business='" + business + '\'' +
                ", product='" + product + '\'' +
                ", releaseUsers=" + releaseUsers +
                ", editUsers=" + editUsers +
                ", accessUsers=" + accessUsers +
                ", description='" + description + '\'' +
                ", workspaceId=" + workspaceId +
                ", workspaceName='" + workspaceName + '\'' +
                ", devProcessList=" + devProcessList +
                ", orchestratorModeList=" + orchestratorModeList +
                ", associateGit=" + associateGit +
                ", gitToken=" + gitToken +
                ", gitUser=" + gitUser +
                '}';
    }

    public Boolean getAssociateGit() {
        return associateGit;
    }

    public void setAssociateGit(Boolean associateGit) {
        this.associateGit = associateGit;
    }

    public String getCreatorLabel() {
        return creatorLabel;
    }

    public void setCreatorLabel(String creatorLabel) {
        this.creatorLabel = creatorLabel;
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
