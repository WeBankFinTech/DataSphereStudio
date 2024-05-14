package com.webank.wedatasphere.dss.git.common.protocol.response;

public class GitRemoveResponse {
    private String commitId;

    public GitRemoveResponse(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
}
