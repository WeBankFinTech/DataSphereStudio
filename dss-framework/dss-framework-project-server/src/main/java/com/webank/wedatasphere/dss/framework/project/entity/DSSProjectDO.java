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

package com.webank.wedatasphere.dss.framework.project.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;



@TableName(value = "dss_project")
public class DSSProjectDO implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    /**
     * Source of the dss_project
     */
    private String source;

    private String description;

    private Long userId;

    private String username;

    private Long workspaceId;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    /**
     * Organization ID
     */
    private Long orgId;

    private Boolean visibility;

    /**
     * Reserved word
     */
    private Boolean isTransfer;

    private Long initialOrgId;

    /**
     * If it is archived
     */
    @TableField("isArchive")
    private Boolean isArchive;

    private String pic;

    private Integer starNum;

    private String product;

    private Integer applicationArea;

    private String business;

    private Integer isPersonal;

    private String createByStr;

    private String updateByStr;
    /**
     * 开发流程，多个以英文逗号分隔，取得的值是dss_dictionary中的dic_key(parent_key=p_develop_process)，首尾以英文逗号结束
     */
    private String devProcess;

    /**
     * 编码模式，多个以英文逗号分隔，取得的值是dss_dictionary中的dic_key(parent_key=p_arrangement_mode或下面一级)，首尾以英文逗号结束
     */
    private String orchestratorMode;

    private Integer visible;

    /**
     * 1-接入git 0-不接入（默认）
     */
    private Boolean associateGit;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String dataSourceListJson;

    /**
     * 标签
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String label;

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
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

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
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

    public Long getInitialOrgId() {
        return initialOrgId;
    }

    public void setInitialOrgId(Long initialOrgId) {
        this.initialOrgId = initialOrgId;
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

    public Integer getStarNum() {
        return starNum;
    }

    public void setStarNum(Integer starNum) {
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

    public Integer getIsPersonal() {
        return isPersonal;
    }

    public void setIsPersonal(Integer isPersonal) {
        this.isPersonal = isPersonal;
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

    public String getDevProcess() {
        return devProcess;
    }

    public void setDevProcess(String devProcess) {
        this.devProcess = devProcess;
    }

    public String getOrchestratorMode() {
        return orchestratorMode;
    }

    public void setOrchestratorMode(String orchestratorMode) {
        this.orchestratorMode = orchestratorMode;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public String getDataSourceListJson() {
        return dataSourceListJson;
    }

    public void setDataSourceListJson(String dataSourceListJson) {
        this.dataSourceListJson = dataSourceListJson;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "DSSProjectDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", description='" + description + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", workspaceId=" + workspaceId +
                ", createTime=" + createTime +
                ", createBy='" + createBy + '\'' +
                ", updateTime=" + updateTime +
                ", updateBy='" + updateBy + '\'' +
                ", orgId=" + orgId +
                ", visibility=" + visibility +
                ", isTransfer=" + isTransfer +
                ", initialOrgId=" + initialOrgId +
                ", isArchive=" + isArchive +
                ", pic='" + pic + '\'' +
                ", starNum=" + starNum +
                ", product='" + product + '\'' +
                ", applicationArea=" + applicationArea +
                ", business='" + business + '\'' +
                ", isPersonal=" + isPersonal +
                ", createByStr='" + createByStr + '\'' +
                ", updateByStr='" + updateByStr + '\'' +
                ", devProcess='" + devProcess + '\'' +
                ", orchestratorMode='" + orchestratorMode + '\'' +
                ", visible=" + visible +
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
