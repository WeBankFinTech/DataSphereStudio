package com.webank.wedatasphere.dss.git.common.protocol.request;


public class GitUserInfoRequest {
    private Long workspaceId;
    private String type;

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
