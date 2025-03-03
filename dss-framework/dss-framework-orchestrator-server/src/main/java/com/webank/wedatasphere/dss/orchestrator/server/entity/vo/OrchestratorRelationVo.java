package com.webank.wedatasphere.dss.orchestrator.server.entity.vo;

public class OrchestratorRelationVo {
    private Long orchestratorId;
    private Long flowId;

    public OrchestratorRelationVo() {
    }

    public OrchestratorRelationVo(Long flowId, Long orchestratorId) {
        this.flowId = flowId;
        this.orchestratorId = orchestratorId;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }
}
