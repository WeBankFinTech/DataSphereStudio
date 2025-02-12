package com.webank.wedatasphere.dss.orchestrator.server.entity.request;

import java.util.List;

public class BatchPublishFlowCheckRequest {

    private List<Long> orchestratorIdList;


    public List<Long> getOrchestratorIdList() {
        return orchestratorIdList;
    }

    public void setOrchestratorIdList(List<Long> orchestratorIdList) {
        this.orchestratorIdList = orchestratorIdList;
    }
}
