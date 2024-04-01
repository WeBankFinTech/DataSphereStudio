package com.webank.wedatasphere.dss.git.common.protocol.request;


public class GitCheckProjectRequest extends GitBaseRequest {
    /**
     * DSS用户名
     */
    private String username;



    public GitCheckProjectRequest() {
    }

    public GitCheckProjectRequest(String username) {
        this.username = username;
    }

    public GitCheckProjectRequest(Long workspaceId, String projectName, String username) {
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
