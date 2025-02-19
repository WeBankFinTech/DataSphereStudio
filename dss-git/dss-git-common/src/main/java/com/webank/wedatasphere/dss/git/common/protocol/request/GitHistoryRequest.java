package com.webank.wedatasphere.dss.git.common.protocol.request;

public class GitHistoryRequest extends GitBaseRequest{
    private String filePath;

    public GitHistoryRequest() {
    }

    public GitHistoryRequest(Long workspaceId, String projectName) {
        super(workspaceId, projectName);
    }

    public GitHistoryRequest(String filePath) {
        this.filePath = filePath;
    }

    public GitHistoryRequest(Long workspaceId, String projectName, String filePath) {
        super(workspaceId, projectName);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
