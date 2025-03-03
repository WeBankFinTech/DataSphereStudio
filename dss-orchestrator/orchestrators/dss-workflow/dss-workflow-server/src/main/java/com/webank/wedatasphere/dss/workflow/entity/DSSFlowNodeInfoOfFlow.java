package com.webank.wedatasphere.dss.workflow.entity;

public class DSSFlowNodeInfoOfFlow {

    /**
     *(子)工作流ID
     */
    private Long appId;
    /**
     * 编排ID
     */
    private Long orchestratorId;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }
}
