/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.common.entity.project;

import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.flow.DSSFlow;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by enjoyyin on 2019/9/16.
 */
public class DSSProject implements Project {

    private Long id;
    private String name;
    private String description;
    private String source;
    private Long userID;
    private Date createTime;
    private Long createBy;
    private Date updateTime;
    private Long updateBy;
    private Long orgID;
    private Boolean visibility;
    private Boolean isTransfer;
    private Boolean isArchive;
    private Long initialOrgID;
    private String pic;
    private Long starNum;
    private String product;
    private Integer applicationArea;
    private String business;
    private Long workspaceId;

    private DSSProjectVersion latestVersion;
    private Boolean isNotPublish;
    private String userName;

    private String projectGroup;
    private List<ProjectVersion> projectVersions;
    private List<DSSFlow> flows;
    private List<Resource> projectResources;

    public List<Resource> getProjectResources() {
        return projectResources;
    }

    public void setProjectResources(List<Resource> projectResources) {
        this.projectResources = projectResources;
    }

    public List<? extends DSSFlow> getFlows() {
        return flows;
    }

    public void setFlows(List<? extends DSSFlow> flows) {
        this.flows = flows.stream().map(f -> (DSSFlow) f).collect(Collectors.toList());
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getNotPublish() {
        return isNotPublish;
    }

    public void setNotPublish(Boolean notPublish) {
        isNotPublish = notPublish;
    }

    public Long getOrgID() {
        return orgID;
    }

    public void setOrgID(Long orgID) {
        this.orgID = orgID;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public Boolean getTransfer() {
        return isTransfer;
    }

    public void setTransfer(Boolean transfer) {
        isTransfer = transfer;
    }

    public Long getInitialOrgID() {
        return initialOrgID;
    }

    public void setInitialOrgID(Long initialOrgID) {
        this.initialOrgID = initialOrgID;
    }

    public DSSProjectVersion getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(DSSProjectVersion latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getPorjectGroup() {
        return projectGroup;
    }

    @Override
    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    @Override
    public List<ProjectVersion> getProjectVersions() {
        return projectVersions;
    }

    @Override
    public void setProjectVersions(List<? extends ProjectVersion> projectVersions) {
        this.projectVersions = projectVersions.stream().map(f -> (DSSProjectVersion) f).collect(Collectors.toList());
    }


    @Override
    public void addProjectVersion(ProjectVersion projectVersion) {
        this.projectVersions.add((DSSProjectVersion) projectVersion);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public Boolean getArchive() {
        return isArchive;
    }

    public void setArchive(Boolean archive) {
        isArchive = archive;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public Long getStarNum() {
        return starNum;
    }

    public void setStarNum(Long starNum) {
        this.starNum = starNum;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
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

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }
}
