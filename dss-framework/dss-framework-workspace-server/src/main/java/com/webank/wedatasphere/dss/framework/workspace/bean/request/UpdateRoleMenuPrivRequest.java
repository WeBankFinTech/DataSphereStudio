package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import org.codehaus.jackson.JsonNode;

import java.io.Serializable;
import java.util.Map;

public class UpdateRoleMenuPrivRequest implements Serializable {

    private int menuId;
    private int workspaceId;
    private Map<String,Boolean> menuPrivs;

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Map<String, Boolean> getMenuPrivs() {
        return menuPrivs;
    }

    public void setMenuPrivs(Map<String, Boolean> menuPrivs) {
        this.menuPrivs = menuPrivs;
    }
}
