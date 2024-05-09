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

package com.webank.wedatasphere.dss.orchestrator.common.entity;

import java.util.Date;
import java.util.List;

/**
 * 一个具体的编排实例
 */
public class DSSOrchestratorInfo implements DSSOrchestration {

    private Long id;
    /**
     * 编排名
     */
    private String name;
    /**
     * 编排具体的实现类型，比如工作流workflow
     */
    private String type;
    /**
     * 编排描述
     */
    private String desc;
    /**
     * 创建者
     */
    private String creator;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 用途标签，作为描述信息
     */
    private String uses;
    /**
     * 可以支持的实现的appconn节点
     */
    private String appConnName;
    /**
     * 所属工程
     */
    private Long projectId;
    /**
     * 工作流的唯一id，不同环境里，同一个工作流保持一致，用来判断是否是同一个工作流。
     */
    private String uuid;
    /**
     * 实现的二级类型，比如workflow_DAG
     */
    private String secondaryType;
    /**
     * 所属工作空间
     */
    private Long workspaceId;

    private String orchestratorMode;

    private String orchestratorWay;
    /**
     * 编排的重要程度，调度会考察这个重要程度
     */
    private String orchestratorLevel;
    /**
     * 更新人
     */
    private String updateUser;
    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否默认引用资源参数模板
     */
    private String isDefaultReference;

    /**
     * 工作流状态：save-已保存 push-已提交 publish-已发布
     */
    private String status;

    public DSSOrchestratorInfo() {

    }

    public DSSOrchestratorInfo(String name, String type,
                               String desc, String creator,
                               Date createTime, String uses,
                               String appConnName, Long projectId, String secondaryType,
                               List<String> linkedAppConnNames, String comment) {
        this.name = name;
        this.type = type;
        this.desc = desc;
        this.creator = creator;
        this.createTime = createTime;
        this.uses = uses;
        this.appConnName = appConnName;
        this.projectId = projectId;
        this.secondaryType = secondaryType;
        this.linkedAppConnNames = linkedAppConnNames;
        this.comment = comment;
    }

    public String getSecondaryType() {
        return secondaryType;
    }

    public void setSecondaryType(String secondaryType) {
        this.secondaryType = secondaryType;
    }


    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }


    /**
     * 用来存储该编排可以支持的节点类型名称
     */
    private List<String> linkedAppConnNames;

    public List<String> getLinkedAppConnNames() {
        return linkedAppConnNames;
    }

    public void setLinkedAppConnNames(List<String> linkedAppConnNames) {
        this.linkedAppConnNames = linkedAppConnNames;
    }


    public String getUses() {
        return uses;
    }

    public void setUses(String uses) {
        this.uses = uses;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String comment;

    public String getAppConnName() {
        return appConnName;
    }

    public void setAppConnName(String appConnName) {
        this.appConnName = appConnName;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getOrchestratorMode() {
        return orchestratorMode;
    }

    public void setOrchestratorMode(String orchestratorMode) {
        this.orchestratorMode = orchestratorMode;
    }

    public String getOrchestratorWay() {
        return orchestratorWay;
    }

    public void setOrchestratorWay(String orchestratorWay) {
        this.orchestratorWay = orchestratorWay;
    }

    public String getOrchestratorLevel() {
        return orchestratorLevel;
    }

    public void setOrchestratorLevel(String orchestratorLevel) {
        this.orchestratorLevel = orchestratorLevel;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIsDefaultReference() {
        return isDefaultReference;
    }

    public void setIsDefaultReference(String isDefaultReference) {
        this.isDefaultReference = isDefaultReference;
    }

    @Override
    public String toString() {
        return "DSSOrchestratorInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", desc='" + desc + '\'' +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", uses='" + uses + '\'' +
                ", appConnName='" + appConnName + '\'' +
                ", projectId=" + projectId +
                ", uuid='" + uuid + '\'' +
                ", secondaryType='" + secondaryType + '\'' +
                ", linkedAppConnNames=" + linkedAppConnNames +
                ", comment='" + comment + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
