package com.webank.wedatasphere.dss.orchestrator.common.entity;

public class DSSEncryptOrchestratorCopyInfo extends DSSOrchestratorCopyInfo {


    private Long targetOrchestratorId;

    private Long targetProjectId;

    private Long targetOrchestratorVersionId;


    public Long getTargetOrchestratorId() {
        return targetOrchestratorId;
    }

    public void setTargetOrchestratorId(Long targetOrchestratorId) {
        this.targetOrchestratorId = targetOrchestratorId;
    }

    public Long getTargetProjectId() {
        return targetProjectId;
    }

    public void setTargetProjectId(Long targetProjectId) {
        this.targetProjectId = targetProjectId;
    }


    public Long getTargetOrchestratorVersionId() {
        return targetOrchestratorVersionId;
    }

    public void setTargetOrchestratorVersionId(Long targetOrchestratorVersionId) {
        this.targetOrchestratorVersionId = targetOrchestratorVersionId;
    }
}
