package com.webank.wedatasphere.dss.orchestrator.common.entity;

public class DSSEncryptOrchestratorCopyInfo extends DSSOrchestratorCopyInfo {


    private Long targetOrchestratorId;

    private Long targetProjectId;


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
}
