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

package com.webank.wedatasphere.dss.orchestrator.server.entity.vo;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrchestratorBaseInfo {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 空间id
     */
    private Long workspaceId;

    /**
     * 工程id
     */
    private Long projectId;
    /**
     * 工作流的唯一id，不同环境里，同一个工作流保持一致，用来判断是否是同一个工作流。
     */
    private String uuid;

    /**
     * 编排模式id（工作流,调用orchestrator服务返回的orchestratorId）
     */
    private Long orchestratorId;

    /**
     * 编排模式版本id（工作流,调用orchestrator服务返回的orchestratorVersionId）
     */
    private Long orchestratorVersionId;

    /**
     * 编排名称
     */
    private String orchestratorName;

    /**
     * 编排模式，取得的值是dss_dictionary中的dic_key(parent_key=p_orchestratorment_mode)
     */
    private String orchestratorMode;

    /**
     * 编排方式
     */
    //private String  orchestratorWay;

    /**
     * 用途
     */
    private String uses;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    private List<String> orchestratorWays;

    private String orchestratorLevel;

    private String isDefaultReference;

    private boolean flowEditLockExist = false;

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getReleasable() {
        return releasable;
    }

    public void setReleasable(Boolean releasable) {
        this.releasable = releasable;
    }

    /**
     * 工程权限等级：0-查看，1-编辑，2-发布
     */

    private Boolean editable;

    /**
     * 工作流是否可发布
     */
    private Boolean releasable;

    private String status;

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public Long getOrchestratorVersionId() {
        return orchestratorVersionId;
    }

    public void setOrchestratorVersionId(Long orchestratorVersionId) {
        this.orchestratorVersionId = orchestratorVersionId;
    }

    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }

    public String getOrchestratorMode() {
        return orchestratorMode;
    }

    public void setOrchestratorMode(String orchestratorMode) {
        this.orchestratorMode = orchestratorMode;
    }

    public String getUses() {
        return uses;
    }

    public void setUses(String uses) {
        this.uses = uses;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public List<String> getOrchestratorWays() {
        return orchestratorWays;
    }

    public void setOrchestratorWays(List<String> orchestratorWays) {
        this.orchestratorWays = orchestratorWays;
    }

    public boolean isFlowEditLockExist() {
        return flowEditLockExist;
    }

    public void setFlowEditLockExist(boolean flowEditLockExist) {
        this.flowEditLockExist = flowEditLockExist;
    }

    public String getOrchestratorLevel() {
        return orchestratorLevel;
    }

    public void setOrchestratorLevel(String orchestratorLevel) {
        this.orchestratorLevel = orchestratorLevel;
    }

    public String getIsDefaultReference() {
        return isDefaultReference;
    }

    public void setIsDefaultReference(String isDefaultReference) {
        this.isDefaultReference = isDefaultReference;
    }

    public static OrchestratorBaseInfo convertFrom(DSSOrchestratorInfo dssInfo){
        OrchestratorBaseInfo baseInfo = new OrchestratorBaseInfo();

        // 直接对应的属性
        baseInfo.setOrchestratorVersionId(dssInfo.getId());
        baseInfo.setProjectId(dssInfo.getProjectId());
        baseInfo.setWorkspaceId(dssInfo.getWorkspaceId());
        baseInfo.setOrchestratorName(dssInfo.getName());
        baseInfo.setDescription(dssInfo.getDesc());
        baseInfo.setUses(dssInfo.getUses());
        baseInfo.setCreateUser(dssInfo.getCreator());
        baseInfo.setCreateTime(dssInfo.getCreateTime());
        baseInfo.setUpdateUser(dssInfo.getUpdateUser());
        baseInfo.setUpdateTime(dssInfo.getUpdateTime());

        // 可能需要根据实际情况调整的属性
        baseInfo.setOrchestratorMode(dssInfo.getOrchestratorMode());
        baseInfo.setOrchestratorLevel(dssInfo.getOrchestratorLevel());

        // DSSOrchestratorInfo中的orchestratorWay可能需要映射到OrchestratorBaseInfo的orchestratorWays列表中
        List<String> orchestratorWays = new ArrayList<>();
        orchestratorWays.add(dssInfo.getOrchestratorWay());
        baseInfo.setOrchestratorWays(orchestratorWays);
        return baseInfo;
    }

    public DSSOrchestratorInfo convertToDSSOrchestratorInfo() {
        OrchestratorBaseInfo baseInfo=this;
        DSSOrchestratorInfo dssInfo = new DSSOrchestratorInfo();

        // 直接对应的属性
        dssInfo.setId(baseInfo.getId());
        dssInfo.setProjectId(baseInfo.getProjectId());
        dssInfo.setWorkspaceId(baseInfo.getWorkspaceId());
        dssInfo.setName(baseInfo.getOrchestratorName());
        dssInfo.setDesc(baseInfo.getDescription());
        dssInfo.setUses(baseInfo.getUses());
        dssInfo.setCreator(baseInfo.getCreateUser());
        dssInfo.setCreateTime(baseInfo.getCreateTime());
        dssInfo.setUpdateUser(baseInfo.getUpdateUser());
        dssInfo.setUpdateTime(baseInfo.getUpdateTime());
        dssInfo.setUUID(baseInfo.getUuid());

        // 可能需要根据实际情况调整的属性
        dssInfo.setOrchestratorMode(baseInfo.getOrchestratorMode());
        dssInfo.setOrchestratorLevel(baseInfo.getOrchestratorLevel());

        // 处理没有直接对应的字段
        if (baseInfo.getOrchestratorWays() != null && !baseInfo.getOrchestratorWays().isEmpty()) {
            // 取第一个作为orchestratorWay，这需要根据实际情况来决定是否合适
            dssInfo.setOrchestratorWay(baseInfo.getOrchestratorWays().get(0));
        }

        return dssInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
