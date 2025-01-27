package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import java.io.Serializable;

public class UpdateUserDefaultWorkspaceRequest implements Serializable {

    private  Long workspaceId;

    private String workspaceName;

    private Boolean isDefaultWorkspace;


    private String username;


    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public Boolean getIsDefaultWorkspace() {
        return isDefaultWorkspace;
    }

    public void setIsDefaultWorkspace(Boolean defaultWorkspace) {
        isDefaultWorkspace = defaultWorkspace;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UpdateUserDefaultWorkspaceRequest{" +
                "workspaceId=" + workspaceId +
                ", workspaceName='" + workspaceName + '\'' +
                ", isDefaultWorkspace=" + isDefaultWorkspace +
                ", username='" + username + '\'' +
                '}';
    }
}
