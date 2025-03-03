package com.webank.wedatasphere.dss.orchestrator.common.protocol;


import com.webank.wedatasphere.dss.common.entity.BmlResource;

public class RequestUpdateOrchestratorBML {
    private Long flowId;
    private BmlResource bmlResource;

    public RequestUpdateOrchestratorBML() {
    }

    public RequestUpdateOrchestratorBML(Long flowId, BmlResource bmlResource) {
        this.flowId = flowId;
        this.bmlResource = bmlResource;
    }


    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public BmlResource getBmlResource() {
        return bmlResource;
    }

    public void setBmlResource(BmlResource bmlResource) {
        this.bmlResource = bmlResource;
    }
}
