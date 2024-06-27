package com.webank.wedatasphere.dss.git.common.protocol.request;

public class GitCommitInfoBetweenRequest extends GitBaseRequest{
    private String oldCommitId;
    private String newCommitId;
    private String dirName;

    public GitCommitInfoBetweenRequest(String oldCommitId, String newCommitId, String dirName) {
        this.oldCommitId = oldCommitId;
        this.newCommitId = newCommitId;
        this.dirName = dirName;
    }

    public GitCommitInfoBetweenRequest(Long workspaceId, String projectName, String oldCommitId, String newCommitId, String dirName) {
        super(workspaceId, projectName);
        this.oldCommitId = oldCommitId;
        this.newCommitId = newCommitId;
        this.dirName = dirName;
    }

    public GitCommitInfoBetweenRequest() {
    }

    public String getOldCommitId() {
        return oldCommitId;
    }

    public void setOldCommitId(String oldCommitId) {
        this.oldCommitId = oldCommitId;
    }

    public String getNewCommitId() {
        return newCommitId;
    }

    public void setNewCommitId(String newCommitId) {
        this.newCommitId = newCommitId;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }
}
