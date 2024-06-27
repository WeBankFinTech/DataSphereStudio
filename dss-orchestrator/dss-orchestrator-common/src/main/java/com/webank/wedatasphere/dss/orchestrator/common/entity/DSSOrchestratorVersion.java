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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DSSOrchestratorVersion {
    private final static Logger LOGGER = LoggerFactory.getLogger(DSSOrchestratorVersion.class);


    private Long id;
    /**
     * 编排id
     */
    private Long orchestratorId;
    /**
     * 对应的实现的id，比如workflow的id。每个版本{@link #getVersion()}对应的实现可以不一样。
     */
    private Long appId;
    /**
     * 所属项目id
     */
    private Long projectId;
    /**
     * 创建的来源。比如新建、回滚。 用于记录信息description用，一般不消费。
     */
    private String source;
    /**
     * 编排的版本哈
     */
    private String version;
    /**
     * 备注
     */
    private String comment;
    /**
     * 更新时间
     */
    private Date   updateTime;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 废弃字段。  具体实现的内容已经放到实现模块了。比如工作流在工作流模块，通过关联获取
     */
    private String content;
    /**
     * 这个编排版本所对应的contextId
     */
    private String contextId;
    /**
     * 有效标示 0:无效；1：有效，默认是有效
     */
    private Integer validFlag;
    /**
     * 项目发布当前版本的commitId
     */
    private String commitId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public Integer getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(Integer validFlag) {
        this.validFlag = validFlag;
    }

    /**
     * 提取出contextId
     */
    public void setFormatContextId(String contextId) {
        try {
            if (contextId == null || "".equals(contextId.trim()) || !contextId.contains("value") || !contextId.contains("contextId")) {
                this.contextId = contextId;
            } else {
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(contextId).getAsJsonObject();
                String tempValue = jsonObject.get("value").getAsString();
                String tempContextId = parser.parse(tempValue).getAsJsonObject().get("contextId").getAsString();
                this.contextId = tempContextId;
            }
        } catch (Exception e) {
            LOGGER.error("setFormatContextIdError-", e);
            this.contextId = contextId;
        }
    }

    @Override
    public String toString() {
        return "DSSOrchestratorVersion{" +
                "id=" + id +
                ", orchestratorId=" + orchestratorId +
                ", appId=" + appId +
                ", projectId=" + projectId +
                ", source='" + source + '\'' +
                ", version='" + version + '\'' +
                ", comment='" + comment + '\'' +
                ", updateTime=" + updateTime +
                ", updater='" + updater + '\'' +
                ", content='" + content + '\'' +
                ", contextId='" + contextId + '\'' +
                ", validFlag='" + validFlag + '\'' +
                ", commitId='" + commitId + '\'' +
                '}';
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
}
