package com.webank.wedatasphere.dss.orchestrator.common.protocol;

public class RequestQuertByAppIdOrchestrator {
    private Long appId;

    public RequestQuertByAppIdOrchestrator(Long appId) {
        this.appId = appId;
    }

    public RequestQuertByAppIdOrchestrator() {
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }
}
