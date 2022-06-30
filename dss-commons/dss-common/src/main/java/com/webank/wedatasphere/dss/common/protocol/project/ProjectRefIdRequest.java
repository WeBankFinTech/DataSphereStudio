package com.webank.wedatasphere.dss.common.protocol.project;

public class ProjectRefIdRequest {
    private Long appInstanceId;
    private Long dssProjectId;

    public ProjectRefIdRequest(Long appInstanceId, Long dssProjectId) {
        this.appInstanceId = appInstanceId;
        this.dssProjectId = dssProjectId;
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


}
