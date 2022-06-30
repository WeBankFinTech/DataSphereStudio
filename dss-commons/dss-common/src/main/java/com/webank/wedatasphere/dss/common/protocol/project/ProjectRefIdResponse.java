package com.webank.wedatasphere.dss.common.protocol.project;

public class ProjectRefIdResponse {
    private Long appInstanceId;
    private Long dssProjectId;
    private Long refProjectId;

    public ProjectRefIdResponse(Long appInstanceId, Long dssProjectId, Long refProjectId) {
        this.appInstanceId = appInstanceId;
        this.dssProjectId = dssProjectId;
        this.refProjectId = refProjectId;
    }

    public Long getAppInstanceId() {
        return appInstanceId;
    }

    public void setAppInstanceId(Long appInstanceId) {
        this.appInstanceId = appInstanceId;
    }

    public Long getDssProjectId() {
        return dssProjectId;
    }

    public void setDssProjectId(Long dssProjectId) {
        this.dssProjectId = dssProjectId;
    }

    public Long getRefProjectId() {
        return refProjectId;
    }

    public void setRefProjectId(Long refProjectId) {
        this.refProjectId = refProjectId;
    }


}
