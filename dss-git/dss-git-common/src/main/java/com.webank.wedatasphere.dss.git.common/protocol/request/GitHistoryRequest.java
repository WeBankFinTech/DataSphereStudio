package com.webank.wedatasphere.dss.git.common.protocol.request;

public class GitHistoryRequest extends GitBaseRequest{
    private String startCommitId;
    private String endCommitId;

    public GitHistoryRequest(String startCommitId, String endCommitId) {
        this.startCommitId = startCommitId;
        this.endCommitId = endCommitId;
    }

    public GitHistoryRequest(Long workspaceId, String projectName, String startCommitId, String endCommitId) {
        super(workspaceId, projectName);
        this.startCommitId = startCommitId;
        this.endCommitId = endCommitId;
    }

    public GitHistoryRequest() {
    }

    public GitHistoryRequest(Long workspaceId, String projectName) {
        super(workspaceId, projectName);
    }

    public String getStartCommitId() {
        return startCommitId;
    }

    public void setStartCommitId(String startCommitId) {
        this.startCommitId = startCommitId;
    }

    public String getEndCommitId() {
        return endCommitId;
    }

    public void setEndCommitId(String endCommitId) {
        this.endCommitId = endCommitId;
    }
}
