package com.webank.wedatasphere.dss.orchestrator.common.entity;

public class DSSOrchestratorRefOrchestration {

    private Long id;
    private Long orchestratorId;
    private Long refProjectId;
    private Long refOrchestrationId;

    public DSSOrchestratorRefOrchestration(Long orchestratorId, Long refProjectId, Long refOrchestrationId) {
        this.orchestratorId = orchestratorId;
        this.refProjectId = refProjectId;
        this.refOrchestrationId = refOrchestrationId;
    }

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

    public Long getRefProjectId() {
        return refProjectId;
    }

    public void setRefProjectId(Long refProjectId) {
        this.refProjectId = refProjectId;
    }

    public Long getRefOrchestrationId() {
        return refOrchestrationId;
    }

    public void setRefOrchestrationId(Long refOrchestrationId) {
        this.refOrchestrationId = refOrchestrationId;
    }
}
