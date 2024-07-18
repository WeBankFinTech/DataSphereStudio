package com.webank.wedatasphere.dss.git.common.protocol.request;

import com.webank.wedatasphere.dss.common.entity.BmlResource;

import java.util.List;

public class GitBatchCommitRequest extends GitBaseRequest{
    private String comment;
    private String username;
    private List<String> filePath;
    private BmlResource bmlResource;

    public GitBatchCommitRequest(String comment, String username, List<String> filePath, BmlResource bmlResource) {
        this.comment = comment;
        this.username = username;
        this.filePath = filePath;
        this.bmlResource = bmlResource;
    }

    public GitBatchCommitRequest(Long workspaceId, String projectName, String comment, String username, List<String> filePath, BmlResource bmlResource) {
        super(workspaceId, projectName);
        this.comment = comment;
        this.username = username;
        this.filePath = filePath;
        this.bmlResource = bmlResource;
    }

    public GitBatchCommitRequest() {
    }

    public GitBatchCommitRequest(Long workspaceId, String projectName) {
        super(workspaceId, projectName);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getFilePath() {
        return filePath;
    }

    public void setFilePath(List<String> filePath) {
        this.filePath = filePath;
    }

    public BmlResource getBmlResource() {
        return bmlResource;
    }

    public void setBmlResource(BmlResource bmlResource) {
        this.bmlResource = bmlResource;
    }
}
