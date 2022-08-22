package com.webank.wedatasphere.dss.orchestrator.server.entity.orchestratorCopy;

import com.webank.wedatasphere.dss.common.entity.BmlResource;

import java.util.List;

public class OrchestratorExportResult {

    private List<BmlResource> bmlResourceList;

    public List<BmlResource> getBmlResourceList() {
        return bmlResourceList;
    }

    public void setBmlResourceList(List<BmlResource> bmlResourceList) {
        this.bmlResourceList = bmlResourceList;
    }

}
