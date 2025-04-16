package com.webank.wedatasphere.dss.common.protocol.project;

public class ProjectUserAuthModifyRequest {

    private Long projectId;

    private String projectName;

    private String accessUser;

    private String workspaceName;

    private Long workspaceId;

    private String username;


    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getAccessUser() {
        return accessUser;
    }

    public void setAccessUser(String accessUser) {
        this.accessUser = accessUser;
    }


    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    @Override
    public String toString() {
        return "ProjectUserAuthModifyRequest{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", accessUser='" + accessUser + '\'' +
                ", workspaceName='" + workspaceName + '\'' +
                ", workspaceId=" + workspaceId +
                ", username='" + username + '\'' +
                '}';
    }
}
