package com.webank.wedatasphere.dss.git.common.protocol.request;

public class GitDiffTargetCommitRequest extends GitBaseRequest{
    private String commitId;
    private String username;
    private String filePath;

    public GitDiffTargetCommitRequest(String commitId, String username, String filePath) {
        this.commitId = commitId;
        this.username = username;
        this.filePath = filePath;
    }

    public GitDiffTargetCommitRequest(Long workspaceId, String projectName, String commitId, String username, String filePath) {
        super(workspaceId, projectName);
        this.commitId = commitId;
        this.username = username;
        this.filePath = filePath;
    }

    public GitDiffTargetCommitRequest() {
    }

    public GitDiffTargetCommitRequest(Long workspaceId, String projectName) {
        super(workspaceId, projectName);
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
