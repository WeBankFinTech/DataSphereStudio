package com.webank.wedatasphere.dss.orchestrator.common.protocol;


public class RequestPushOrchestrator {
    private Long flowId;

    public RequestPushOrchestrator(Long flowId) {
        this.flowId = flowId;
    }

    public RequestPushOrchestrator() {
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }
}
