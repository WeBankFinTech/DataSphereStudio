package com.webank.wedatasphere.dss.git.common.protocol.response;


public class GitRevertResponse {
    private String commitId;

    public GitRevertResponse(String commitId) {
        this.commitId = commitId;
    }

    public GitRevertResponse() {
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
}
