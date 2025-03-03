package com.webank.wedatasphere.dss.git.common.protocol.request;


public class GitArchiveProjectRequest extends GitBaseRequest{
    /**
     * DSS用户名
     */
    private String username;

    public GitArchiveProjectRequest() {
    }

    public GitArchiveProjectRequest(String username) {
        this.username = username;
    }

    public GitArchiveProjectRequest(Long workspaceId, String projectName, String username) {
        super(workspaceId, projectName);
        this.username = username;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
