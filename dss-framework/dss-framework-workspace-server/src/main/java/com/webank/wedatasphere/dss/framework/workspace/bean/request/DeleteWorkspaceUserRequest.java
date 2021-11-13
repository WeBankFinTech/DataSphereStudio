package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import java.io.Serializable;

public class DeleteWorkspaceUserRequest implements Serializable {

    private int workspaceId;
    private String username ;

    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
