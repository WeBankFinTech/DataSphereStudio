package com.webank.wedatasphere.dss.framework.workspace.client.entity;

import java.io.Serializable;

public class DSSWorkspaceRole implements Serializable {
    private static final long serialVersionUID=1L;

    private int roleId;
    private String roleName;
    private String roleFrontName;


    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleFrontName() {
        return roleFrontName;
    }

    public void setRoleFrontName(String roleFrontName) {
        this.roleFrontName = roleFrontName;
    }

    @Override
    public String toString() {
        return "DSSWorkspaceRole{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", roleFrontName='" + roleFrontName + '\'' +
                '}';
    }
}
