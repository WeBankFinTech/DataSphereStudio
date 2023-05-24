package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import java.io.Serializable;
import java.util.List;

public class UpdateWorkspaceUserRequest implements Serializable {

    private int workspaceId;
    private String userName ;
    private List<Integer> roles;
    private String userId;

    /**
     * 0表示实名用户，1表示非实名用户
     */
    private Integer isReal;

    public Integer getIsReal() {
        return isReal;
    }

    public void setIsReal(Integer isReal) {
        this.isReal = isReal;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    @Override
    public String toString() {
        return "UpdateWorkspaceUserRequest{" +
                "workspaceId=" + workspaceId +
                ", userName='" + userName + '\'' +
                ", roles=" + roles +
                ", userId='" + userId + '\'' +
                ", isReal='" + isReal + '\'' +
                '}';
    }
}
