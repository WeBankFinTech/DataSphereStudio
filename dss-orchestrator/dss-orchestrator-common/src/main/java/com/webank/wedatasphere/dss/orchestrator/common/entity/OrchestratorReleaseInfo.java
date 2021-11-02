package com.webank.wedatasphere.dss.orchestrator.common.entity;

import java.util.Date;

/**
 * The type Orchestrator release info.
 *
 * @author yuxin.yuan
 * @date 2021/10/27
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
        String orchestratorVersion, Long orchestratorVersionAppId) {
        OrchestratorReleaseInfo instance = new OrchestratorReleaseInfo();
        instance.setOrchestratorId(orchestratorId);
        instance.setOrchestratorVersionId(orchestratorVersionId);
        instance.setOrchestratorVersion(orchestratorVersion);
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
