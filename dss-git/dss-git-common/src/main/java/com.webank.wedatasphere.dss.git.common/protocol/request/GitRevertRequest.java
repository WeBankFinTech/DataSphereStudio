package com.webank.wedatasphere.dss.git.common.protocol.request;



public class GitRevertRequest extends GitBaseRequest{
    private String commitId;
    private String path;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    public GitRevertRequest(Long workspaceId, String projectName, String commitId, String path, String username) {
        super(workspaceId, projectName);
        this.commitId = commitId;
        this.path = path;
        this.username = username;
    }

    public GitRevertRequest() {
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
