package com.webank.wedatasphere.dss.git.common.protocol.request;

public class GitCurrentCommitRequest extends GitBaseRequest{
    private String username;

    public GitCurrentCommitRequest(String username) {
        this.username = username;
    }

    public GitCurrentCommitRequest(Long workspaceId, String projectName, String username) {
        super(workspaceId, projectName);
        this.username = username;
    }

    public GitCurrentCommitRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
