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

package com.webank.wedatasphere.dss.orchestrator.common.entity;

import java.util.Date;
import java.util.List;

/**
 * created by cooperyang on 2020/10/16
 * Description:
 */
public class DSSOrchestratorInfo {

    private Long id;

    private String name;

    private String type;

    private String desc;

    private String creator;

    private Date createTime;

    private String uses;

    private String appConnName;

    private Long projectId;

    private String uuid;

    private String secondaryType;


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
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
}
