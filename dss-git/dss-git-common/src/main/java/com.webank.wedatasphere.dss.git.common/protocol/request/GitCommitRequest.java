package com.webank.wedatasphere.dss.git.common.protocol.request;

import com.webank.wedatasphere.dss.common.entity.BmlResource;

import java.util.List;
import java.util.Map;


public class GitCommitRequest extends GitBaseRequest{
    /**
     * 待修改的文件（key：文件相对路径，value：文件对应BML）
     */
    private Map<String, BmlResource> bmlResourceMap;
    /**
     * 提交更新时的comment
     */
    private String comment;
    /**
     * 下载BMLReource使用的用户名
     */
    private String username;
    private String gitUser;
    private String gitToken;


    public GitCommitRequest() {
    }

    public GitCommitRequest(Map<String, BmlResource> bmlResourceMap, String comment, String username, String gitUser, String gitToken) {
        this.bmlResourceMap = bmlResourceMap;
        this.comment = comment;
        this.username = username;
        this.gitUser = gitUser;
        this.gitToken = gitToken;
    }

    public GitCommitRequest(Long workspaceId, String projectName, Map<String, BmlResource> bmlResourceMap, String comment, String username, String gitUser, String gitToken) {
        super(workspaceId, projectName);
        this.bmlResourceMap = bmlResourceMap;
        this.comment = comment;
        this.username = username;
        this.gitUser = gitUser;
        this.gitToken = gitToken;
    }

    public GitCommitRequest(Long workspaceId, String projectName, Map<String, BmlResource> bmlResourceMap, String comment, String username) {
        super(workspaceId, projectName);
        this.bmlResourceMap = bmlResourceMap;
        this.comment = comment;
        this.username = username;
    }

    public GitCommitRequest(Map<String, BmlResource> bmlResourceMap, String comment, String username) {
        this.bmlResourceMap = bmlResourceMap;
        this.comment = comment;
        this.username = username;
    }


    public Map<String, BmlResource> getBmlResourceMap() {
        return bmlResourceMap;
    }

    public void setBmlResourceMap(Map<String, BmlResource> bmlResourceMap) {
        this.bmlResourceMap = bmlResourceMap;
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

    public String getGitUser() {
        return gitUser;
    }

    public void setGitUser(String gitUser) {
        this.gitUser = gitUser;
    }

    public String getGitToken() {
        return gitToken;
    }

    public void setGitToken(String gitToken) {
        this.gitToken = gitToken;
    }
}
