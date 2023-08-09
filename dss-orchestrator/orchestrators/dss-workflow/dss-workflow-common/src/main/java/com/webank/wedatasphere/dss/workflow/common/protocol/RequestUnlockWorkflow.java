package com.webank.wedatasphere.dss.workflow.common.protocol;

public class RequestUnlockWorkflow {
    private String username;
    private Long flowId;
    private Boolean confirmDelete;

    public RequestUnlockWorkflow(String username, Long flowId, boolean confirmDelete) {
        this.username = username;
        this.flowId = flowId;
        this.confirmDelete = confirmDelete;
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
}
