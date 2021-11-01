package com.webank.wedatasphere.dss.workflow.entity;

/**
 * The type Published process definition status.
 *
 * @author yuxin.yuan
 * @date 2021/10/20
 */
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
