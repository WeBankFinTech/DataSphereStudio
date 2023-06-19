package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import java.io.Serializable;

public class DeleteWorkspaceUserRequest implements Serializable {

    private int workspaceId;
    private String userName;

    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    @Override
    public String toString() {
        return "DeleteWorkspaceUserRequest{" +
                "workspaceId=" + workspaceId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
