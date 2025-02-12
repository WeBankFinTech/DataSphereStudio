package com.webank.wedatasphere.dss.orchestrator.common.protocol;

public class RequestOrchestratorVersionUpdateTime {
    private Long orchestratorId;

    public RequestOrchestratorVersionUpdateTime(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public RequestOrchestratorVersionUpdateTime() {
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }
}
