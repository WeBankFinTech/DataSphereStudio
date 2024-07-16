package com.webank.wedatasphere.dss.orchestrator.server.entity.request;


import java.util.List;

public class OrchestratorBatchSubmitRequest {
    private List<OrchestratorSubmitRequest> submitRequestList;

    public OrchestratorBatchSubmitRequest(List<OrchestratorSubmitRequest> submitRequestList) {
        this.submitRequestList = submitRequestList;
    }

    public OrchestratorBatchSubmitRequest() {
    }

    public List<OrchestratorSubmitRequest> getSubmitRequestList() {
        return submitRequestList;
    }

    public void setSubmitRequestList(List<OrchestratorSubmitRequest> submitRequestList) {
        this.submitRequestList = submitRequestList;
    }
}
