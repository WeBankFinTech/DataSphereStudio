package com.webank.wedatasphere.dss.git.common.protocol.request;

public class GitBaseRequest{
    private Long workspaceId;
    private String projectName;

    public GitBaseRequest() {
    }

    public GitBaseRequest(Long workspaceId, String projectName) {
        this.workspaceId = workspaceId;
        this.projectName = projectName;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
