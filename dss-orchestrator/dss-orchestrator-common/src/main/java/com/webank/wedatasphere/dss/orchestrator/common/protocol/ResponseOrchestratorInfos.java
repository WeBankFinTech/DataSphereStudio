package com.webank.wedatasphere.dss.orchestrator.common.protocol;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;

import java.util.List;

public class ResponseOrchestratorInfos {
    List<DSSOrchestratorInfo> orchestratorInfos;

    public ResponseOrchestratorInfos(List<DSSOrchestratorInfo> orchestratorInfos) {
        this.orchestratorInfos = orchestratorInfos;
    }

    public List<DSSOrchestratorInfo> getOrchestratorInfos() {
        return orchestratorInfos;
    }

    public void setOrchestratorInfos(List<DSSOrchestratorInfo> orchestratorInfos) {
        this.orchestratorInfos = orchestratorInfos;
    }
}
