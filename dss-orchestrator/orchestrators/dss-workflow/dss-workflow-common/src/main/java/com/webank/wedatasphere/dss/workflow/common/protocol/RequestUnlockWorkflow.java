package com.webank.wedatasphere.dss.workflow.common.protocol;

import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

public class RequestUnlockWorkflow {
    private String username;
    private Long flowId;
    private Boolean confirmDelete;

    private Workspace workspace;

    public RequestUnlockWorkflow(String username, Long flowId, boolean confirmDelete) {
        this.username = username;
        this.flowId = flowId;
        this.confirmDelete = confirmDelete;
    }

    public RequestUnlockWorkflow(String username, Long flowId, Boolean confirmDelete, Workspace workspace) {
        this.username = username;
        this.flowId = flowId;
        this.confirmDelete = confirmDelete;
        this.workspace = workspace;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Boolean getConfirmDelete() {
        return confirmDelete;
    }

    public void setConfirmDelete(Boolean confirmDelete) {
        this.confirmDelete = confirmDelete;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
