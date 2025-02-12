package com.webank.wedatasphere.dss.git.common.protocol.request;


public class GitUserByWorkspaceIdRequest extends GitBaseRequest{
    private String username;

    public GitUserByWorkspaceIdRequest(String username) {
        this.username = username;
    }

    public GitUserByWorkspaceIdRequest(Long workspaceId, String projectName, String username) {
        super(workspaceId, projectName);
        this.username = username;
    }

    public GitUserByWorkspaceIdRequest() {
    }

    public GitUserByWorkspaceIdRequest(Long workspaceId, String projectName) {
        super(workspaceId, projectName);
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
