package com.webank.wedatasphere.dss.workflow.entity.request;

import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;

import java.util.Map;

public class EditFlowRequest {
    private Long id;
    private Long orchestratorId;
    private String nodeKey;
    private String params;
    private DSSFlow dssFlow;

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

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public DSSFlow getDssFlow() {
        return dssFlow;
    }

    public void setDssFlow(DSSFlow dssFlow) {
        this.dssFlow = dssFlow;
    }
}
