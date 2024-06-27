package com.webank.wedatasphere.dss.git.common.protocol.response;


public class GitCommitResponse{
    private String commitId;
    private String commitTime;
    private String commitUser;
    private String comment;

    public GitCommitResponse(String commitId, String commitTime, String commitUser, String comment) {
        this.commitId = commitId;
        this.commitTime = commitTime;
        this.commitUser = commitUser;
        this.comment = comment;
    }

    public GitCommitResponse() {
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(String commitTime) {
        this.commitTime = commitTime;
    }

    public String getCommitUser() {
        return commitUser;
    }

    public void setCommitUser(String commitUser) {
        this.commitUser = commitUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
