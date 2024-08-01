package com.webank.wedatasphere.dss.git.common.protocol.request;


public class GitCheckProjectRequest extends GitBaseRequest {
    /**
     * DSS用户名
     */
    private String username;
    private String gitUser;
    private String gitToken;



    public GitCheckProjectRequest() {
    }

    public GitCheckProjectRequest(String username) {
        this.username = username;
    }

    public GitCheckProjectRequest(Long workspaceId, String projectName, String username) {
        super(workspaceId, projectName);
        this.username = username;
    }

    public GitCheckProjectRequest(String username, String gitUser, String gitToken) {
        this.username = username;
        this.gitUser = gitUser;
        this.gitToken = gitToken;
    }

    public GitCheckProjectRequest(Long workspaceId, String projectName, String username, String gitUser, String gitToken) {
        super(workspaceId, projectName);
        this.username = username;
        this.gitUser = gitUser;
        this.gitToken = gitToken;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGitUser() {
        return gitUser;
    }

    public void setGitUser(String gitUser) {
        this.gitUser = gitUser;
    }

    public String getGitToken() {
        return gitToken;
    }

    public void setGitToken(String gitToken) {
        this.gitToken = gitToken;
    }
}
