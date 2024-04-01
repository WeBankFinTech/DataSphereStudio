package com.webank.wedatasphere.dss.git.common.protocol.request;


public class GitFileContentRequest extends GitBaseRequest{
    private String commitId;
    /**
     * 需获取内容的文件相对路径
     */
    private String filePath;

    public GitFileContentRequest() {
    }

    public GitFileContentRequest(String commitId, String filePath) {
        this.commitId = commitId;
        this.filePath = filePath;
    }

    public GitFileContentRequest(Long workspaceId, String projectName, String commitId, String filePath) {
        super(workspaceId, projectName);
        this.commitId = commitId;
        this.filePath = filePath;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
