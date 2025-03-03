package com.webank.wedatasphere.dss.orchestrator.common.entity;


import java.util.List;

public class DSSOrchestratorInfoList {
    private List<DSSOrchestratorInfo> orchestratorInfos;

    public DSSOrchestratorInfoList(List<DSSOrchestratorInfo> orchestratorInfos) {
        this.orchestratorInfos = orchestratorInfos;
    }

    public DSSOrchestratorInfoList() {
    }

    public List<DSSOrchestratorInfo> getOrchestratorInfos() {
        return orchestratorInfos;
    }

    public void setOrchestratorInfos(List<DSSOrchestratorInfo> orchestratorInfos) {
        this.orchestratorInfos = orchestratorInfos;
    }
}
