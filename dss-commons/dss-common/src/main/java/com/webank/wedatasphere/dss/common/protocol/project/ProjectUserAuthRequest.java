package com.webank.wedatasphere.dss.common.protocol.project;

public class ProjectUserAuthRequest {
    private Long projectId;
    private String userName;

    public ProjectUserAuthRequest(Long projectId, String userName) {
        this.projectId = projectId;
        this.userName = userName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
