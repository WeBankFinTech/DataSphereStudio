package com.webank.wedatasphere.dss.orchestrator.common.protocol;


public class ResponsePushOrchestrator {
    private String status;

    public ResponsePushOrchestrator(String status) {
        this.status = status;
    }

    public ResponsePushOrchestrator() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
