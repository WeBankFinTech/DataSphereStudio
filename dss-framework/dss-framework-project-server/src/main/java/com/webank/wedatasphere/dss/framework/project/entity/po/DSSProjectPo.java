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

package com.webank.wedatasphere.dss.framework.project.entity.po;

import java.util.Date;
import java.util.List;

/**
 * created by cooperyang on 2020/11/26
 * Description:
 */
public class DSSProjectPo {


    private Long id;
    private String name;
    private String description;
    private String source;
    private Date createTime;
    private String createBy; //兼容Visualis  后修改类型
    private Date updateTime;
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
    private Integer workspaceId;
    private String projectGroup;
    private String workspaceName;
    private String username;
    private Integer isPersonal;

    private Long userID;
    private String createByStr;
    private String updateByStr;

    private List<String> accessUsers;
    private List<String> editUsers;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

    public Boolean getArchive() {
        return isArchive;
    }

    public void setArchive(Boolean archive) {
        isArchive = archive;
    }

    public Long getInitialOrgID() {
        return initialOrgID;
    }

    public void setInitialOrgID(Long initialOrgID) {
        this.initialOrgID = initialOrgID;
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

    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public List<String> getAccessUsers() {
        return accessUsers;
    }

    public void setAccessUsers(List<String> accessUsers) {
        this.accessUsers = accessUsers;
    }

    public List<String> getEditUsers() {
        return editUsers;
    }

    public void setEditUsers(List<String> editUsers) {
        this.editUsers = editUsers;
    }


    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getIsPersonal() {
        return isPersonal;
    }

    public void setIsPersonal(Integer isPersonal) {
        this.isPersonal = isPersonal;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getCreateByStr() {
        return createByStr;
    }

    public void setCreateByStr(String createByStr) {
        this.createByStr = createByStr;
    }

    public String getUpdateByStr() {
        return updateByStr;
    }

    public void setUpdateByStr(String updateByStr) {
        this.updateByStr = updateByStr;
    }
}
