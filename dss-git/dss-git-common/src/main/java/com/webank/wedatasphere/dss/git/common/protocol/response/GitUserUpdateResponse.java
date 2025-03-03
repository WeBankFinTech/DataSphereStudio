package com.webank.wedatasphere.dss.git.common.protocol.response;


public class GitUserUpdateResponse {
    private int status;
    private String msg;

    private Long workspaceId;

    public GitUserUpdateResponse(int status, String msg, Long workspaceId) {
        this.status = status;
        this.msg = msg;
        this.workspaceId = workspaceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }
}
