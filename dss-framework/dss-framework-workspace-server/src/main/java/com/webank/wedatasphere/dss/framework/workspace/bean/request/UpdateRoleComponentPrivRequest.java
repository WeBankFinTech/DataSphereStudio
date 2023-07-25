package com.webank.wedatasphere.dss.framework.workspace.bean.request;


import java.io.Serializable;
import java.util.Map;

public class UpdateRoleComponentPrivRequest implements Serializable {

    public int componentId;
    public int workspaceId;
    public Map<String,Boolean> componentPrivs;

    public int getComponentId() {
        return componentId;
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Map<String, Boolean> getComponentPrivs() {
        return componentPrivs;
    }

    public void setComponentPrivs(Map<String, Boolean> componentPrivs) {
        this.componentPrivs = componentPrivs;
    }

    @Override
    public String toString() {
        return "UpdateRoleComponentPrivRequest{" +
                "componentId=" + componentId +
                ", workspaceId=" + workspaceId +
                ", componentPrivs=" + componentPrivs +
                '}';
    }
}
