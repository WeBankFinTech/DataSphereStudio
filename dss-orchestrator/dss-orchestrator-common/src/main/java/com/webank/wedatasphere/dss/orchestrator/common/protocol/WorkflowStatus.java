package com.webank.wedatasphere.dss.orchestrator.common.protocol;

public class WorkflowStatus {

    private Boolean published;

    private String releaseState;

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getReleaseState() {
        return releaseState;
    }

    public void setReleaseState(String releaseState) {
        this.releaseState = releaseState;
    }

    public boolean isOnline(String releaseState) {
        return "ONLINE".equalsIgnoreCase(releaseState);
    }
}
