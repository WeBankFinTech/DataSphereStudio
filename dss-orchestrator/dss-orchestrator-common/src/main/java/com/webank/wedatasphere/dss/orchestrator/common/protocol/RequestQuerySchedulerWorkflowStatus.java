package com.webank.wedatasphere.dss.orchestrator.common.protocol;

public class RequestQuerySchedulerWorkflowStatus {
    private String username;
    private Long orchestratorId;

    public RequestQuerySchedulerWorkflowStatus(String username, Long orchestratorId) {
        this.username = username;
        this.orchestratorId = orchestratorId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }
}
