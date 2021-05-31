package com.webank.wedatasphere.dss.framework.release.entity.orchestrator;

/**
 * The type Published process definition status.
 *
 * @author yuxin.yuan
 * @date 2021/05/26
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
}
