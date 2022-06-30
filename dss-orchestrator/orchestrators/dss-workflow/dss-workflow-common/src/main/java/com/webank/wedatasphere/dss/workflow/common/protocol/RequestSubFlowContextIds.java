package com.webank.wedatasphere.dss.workflow.common.protocol;

/**
 * Description
 */

public class RequestSubFlowContextIds {
    private Long flowId;

    public RequestSubFlowContextIds(Long flowId) {
        this.flowId = flowId;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }
}
