package com.webank.wedatasphere.dss.git.common.protocol.request;

import java.util.List;

public class GitDiffFileContentRequest extends GitBaseRequest {


    private String commitId;
    /**
     * 需获取内容的文件相对路径
     */
    private List<String> filePaths;

    private String username;

    private Boolean publish;


    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(List<String> filePaths) {
        this.filePaths = filePaths;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }
}
