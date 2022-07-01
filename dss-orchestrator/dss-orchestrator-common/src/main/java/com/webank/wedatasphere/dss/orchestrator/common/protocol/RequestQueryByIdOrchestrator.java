package com.webank.wedatasphere.dss.orchestrator.common.protocol;

/**
 * Description
 */

public class RequestQueryByIdOrchestrator {
    private Long orchestratorId;
    private Long orcVersionId;

    public RequestQueryByIdOrchestrator(Long orchestratorId, Long orcVersionId) {
        this.orchestratorId = orchestratorId;
        this.orcVersionId = orcVersionId;
    }

    public RequestQueryByIdOrchestrator() {
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public Long getOrcVersionId() {
        return orcVersionId;
    }

    public void setOrcVersionId(Long orcVersionId) {
        this.orcVersionId = orcVersionId;
    }
}
