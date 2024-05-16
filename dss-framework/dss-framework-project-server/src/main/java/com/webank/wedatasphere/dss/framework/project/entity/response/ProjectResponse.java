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

package com.webank.wedatasphere.dss.framework.project.entity.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class ProjectResponse implements Serializable {
    private static final long serialVersionUID=1L;
    //工程id
    private Long id;
    //应用领域
    private Integer applicationArea;
    //业务
    private String business;
    //创建人
    private String createBy;
    //工程描述
    private String description;
    //工程名称
    private String name;
    //
    private String source;
    //产品
    private String product;
    private Boolean isArchive;
    //工程创建时间
    private Date createTime;
    //工程修改时间
    private Date updateTime;

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    private Boolean editable;

    /**
     * 发布用户 list
     */
    private List<String> releaseUsers;

    /**
     * 编辑用户 list
     */
    private List<String> editUsers;

    /**
     * 查看用户 list
     */
    private List<String> accessUsers;

    /**
     * 开发流程 list
     */
    private List<String> devProcessList;

    /**
     * 编码模式 list
     */
    private List<String> orchestratorModeList;

    private Boolean associateGit;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Boolean getArchive() {
        return isArchive;
    }

    public void setArchive(Boolean archive) {
        isArchive = archive;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ResponseProjectVo{" +
                "id=" + id +
                ", applicationArea=" + applicationArea +
                ", business='" + business + '\'' +
                ", createBy='" + createBy + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", product='" + product + '\'' +
                ", isArchive=" + isArchive +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", releaseUsers=" + releaseUsers +
                ", editUsers=" + editUsers +
                ", accessUsers=" + accessUsers +
                ", devProcessList=" + devProcessList +
                ", orchestratorModeList=" + orchestratorModeList +
                ", editable=" + editable +
                ", associateGit=" + associateGit +
                '}';
    }

    public Boolean getAssociateGit() {
        return associateGit;
    }

    public void setAssociateGit(Boolean associateGit) {
        this.associateGit = associateGit;
    }
}
