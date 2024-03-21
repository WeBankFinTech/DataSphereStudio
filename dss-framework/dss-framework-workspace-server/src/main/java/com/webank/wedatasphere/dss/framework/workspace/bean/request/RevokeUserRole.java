package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.Objects;

public class RevokeUserRole {
    @NotBlank(message = "Required String parameter 'userName' is not present")
    private String userName;

    private Integer[] workspaceIds;

    private Integer[] roleIds;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer[] getWorkspaceIds() {
        return workspaceIds;
    }

    public void setWorkspaceIds(Integer[] workspaceIds) {
        this.workspaceIds = workspaceIds;
    }

    public Integer[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Integer[] roleIds) {
        this.roleIds = roleIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RevokeUserRole that = (RevokeUserRole) o;
        return Objects.equals(userName, that.userName) && Arrays.equals(workspaceIds, that.workspaceIds) && Arrays.equals(roleIds, that.roleIds);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(userName);
        result = 31 * result + Arrays.hashCode(workspaceIds);
        result = 31 * result + Arrays.hashCode(roleIds);
        return result;
    }
}
