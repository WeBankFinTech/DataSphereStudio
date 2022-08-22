package com.webank.wedatasphere.dss.orchestrator.publish.entity;

/**
 * Author: xlinliu
 * Date: 2022/8/18
 */
public class OrchestratorExportEntity {
    /**
     * 编排Id
     */
    private Long orchestratorId;
    /**
     * 编排的版本id
     */
    private long versionId;

    public OrchestratorExportEntity() {
    }

    public OrchestratorExportEntity(Long orchestratorId, long versionId) {
        this.orchestratorId = orchestratorId;
        this.versionId = versionId;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }
}
