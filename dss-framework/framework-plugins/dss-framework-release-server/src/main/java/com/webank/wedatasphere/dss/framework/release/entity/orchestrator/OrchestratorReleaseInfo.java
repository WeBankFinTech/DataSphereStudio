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

package com.webank.wedatasphere.dss.framework.release.entity.orchestrator;

import java.util.Date;

/**
 * created by cooperyang on 2020/12/13
 * Description:
 */
public class OrchestratorReleaseInfo {

    private Long id;

    private String orchestratorName;

    private Long orchestratorId;

    private Long orchestratorVersionId;

    private Long orchestratorVersionAppId;

    private String orchestratorVersion;

    private Long schedulerWorkflowId;

    private Date createTime;

    private Date updateTime;

    public static OrchestratorReleaseInfo newInstance(Long orchestratorId, String orchestratorVersion) {
        OrchestratorReleaseInfo orchestratorReleaseInfo = new OrchestratorReleaseInfo();
        orchestratorReleaseInfo.setOrchestratorId(orchestratorId);
        orchestratorReleaseInfo.setOrchestratorVersion(orchestratorVersion);
        return orchestratorReleaseInfo;
    }

    public static OrchestratorReleaseInfo newInstance(Long orchestratorId, Long orchestratorVersionId,
        Long orchestratorVersionAppId) {
        OrchestratorReleaseInfo instance = new OrchestratorReleaseInfo();
        instance.setOrchestratorId(orchestratorId);
        instance.setOrchestratorVersionId(orchestratorVersionId);
        instance.setOrchestratorVersionAppId(orchestratorVersionAppId);
        Date currentTime = new Date(System.currentTimeMillis());
        instance.setCreateTime(currentTime);
        instance.setUpdateTime(currentTime);
        return instance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
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

    public Long getOrchestratorVersionAppId() {
        return orchestratorVersionAppId;
    }

    public void setOrchestratorVersionAppId(Long orchestratorVersionAppId) {
        this.orchestratorVersionAppId = orchestratorVersionAppId;
    }

    public String getOrchestratorVersion() {
        return orchestratorVersion;
    }

    public void setOrchestratorVersion(String orchestratorVersion) {
        this.orchestratorVersion = orchestratorVersion;
    }

    public Long getSchedulerWorkflowId() {
        return schedulerWorkflowId;
    }

    public void setSchedulerWorkflowId(Long schedulerWorkflowId) {
        this.schedulerWorkflowId = schedulerWorkflowId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
