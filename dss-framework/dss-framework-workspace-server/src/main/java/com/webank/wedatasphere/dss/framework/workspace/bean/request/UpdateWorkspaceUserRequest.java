package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import java.io.Serializable;
import java.util.List;

public class UpdateWorkspaceUserRequest implements Serializable {

    private int workspaceId;
    private String username ;
    private List<Integer> roles;
    private String userId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }
}
