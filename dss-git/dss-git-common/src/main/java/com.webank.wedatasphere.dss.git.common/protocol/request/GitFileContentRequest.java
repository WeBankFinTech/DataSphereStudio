package com.webank.wedatasphere.dss.git.common.protocol.request;


import com.webank.wedatasphere.dss.common.entity.BmlResource;

import java.util.Map;

public class GitFileContentRequest extends GitBaseRequest{
    private String commitId;
    /**
     * 需获取内容的文件相对路径
     */
    private String filePath;

    private String username;

    private Boolean publish;

    public GitFileContentRequest() {
    }

    public GitFileContentRequest(Long workspaceId, String projectName) {
        super(workspaceId, projectName);
    }

    public GitFileContentRequest(String commitId, String filePath, String username, Boolean publish) {
        this.commitId = commitId;
        this.filePath = filePath;
        this.username = username;
        this.publish = publish;
    }

    public GitFileContentRequest(Long workspaceId, String projectName, String commitId, String filePath, String username, Boolean publish) {
        super(workspaceId, projectName);
        this.commitId = commitId;
        this.filePath = filePath;
        this.username = username;
        this.publish = publish;
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

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
