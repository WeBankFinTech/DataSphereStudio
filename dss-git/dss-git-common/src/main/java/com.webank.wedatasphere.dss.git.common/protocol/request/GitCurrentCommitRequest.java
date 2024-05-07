package com.webank.wedatasphere.dss.git.common.protocol.request;

public class GitCurrentCommitRequest extends GitBaseRequest{
    private String username;
    private String filepath;


    public GitCurrentCommitRequest() {
    }

    public GitCurrentCommitRequest(String username, String filepath) {
        this.username = username;
        this.filepath = filepath;
    }

    public GitCurrentCommitRequest(Long workspaceId, String projectName, String username, String filepath) {
        super(workspaceId, projectName);
        this.username = username;
        this.filepath = filepath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
