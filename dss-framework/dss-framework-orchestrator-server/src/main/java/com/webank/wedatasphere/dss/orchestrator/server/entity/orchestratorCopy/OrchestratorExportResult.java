package com.webank.wedatasphere.dss.orchestrator.server.entity.orchestratorCopy;

import java.util.List;

public class OrchestratorExportResult {

    private List<BmlResource> bmlResourceList;

    private Long orchestratorVersionId;

    public OrchestratorExportResult(List<BmlResource> bmlResourceList, Long orchestratorVersionId) {
        this.bmlResourceList = bmlResourceList;
        this.orchestratorVersionId = orchestratorVersionId;
    }

    public List<BmlResource> getBmlResourceList() {
        return bmlResourceList;
    }

    public void setBmlResourceList(List<BmlResource> bmlResourceList) {
        this.bmlResourceList = bmlResourceList;
    }

    public Long getOrchestratorVersionId() {
        return orchestratorVersionId;
    }

    public void setOrchestratorVersionId(Long orchestratorVersionId) {
        this.orchestratorVersionId = orchestratorVersionId;
    }
}
