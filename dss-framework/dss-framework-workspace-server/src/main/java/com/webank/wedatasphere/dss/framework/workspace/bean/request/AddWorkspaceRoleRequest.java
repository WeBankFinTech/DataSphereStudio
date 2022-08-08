package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import java.io.Serializable;
import java.util.List;

public class AddWorkspaceRoleRequest implements Serializable {

    private int workspaceId;
    private String roleName ;
    private List<Integer> menuIds;
    private List<Integer> componentIds;

    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<Integer> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<Integer> menuIds) {
        this.menuIds = menuIds;
    }

    public List<Integer> getComponentIds() {
        return componentIds;
    }

    public void setComponentIds(List<Integer> componentIds) {
        this.componentIds = componentIds;
    }

    @Override
    public String toString() {
        return "AddWorkspaceRoleRequest{" +
                "workspaceId=" + workspaceId +
                ", roleName='" + roleName + '\'' +
                ", menuIds=" + menuIds +
                ", componentIds=" + componentIds +
                '}';
    }
}
