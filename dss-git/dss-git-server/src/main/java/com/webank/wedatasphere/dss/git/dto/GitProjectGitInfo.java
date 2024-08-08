package com.webank.wedatasphere.dss.git.dto;

import java.util.Date;

public class GitProjectGitInfo {
    private Long id;
    private Long workspaceId;
    private String gitProjectId;
    private String projectName;
    private String gitUser;
    private String gitToken;
    private String gitUrl;
    private Date createTime;
    private Date updateTime;
    private String gitTokenEncrypt;

    public GitProjectGitInfo() {
    }

    public GitProjectGitInfo(Long id, Long workspaceId, String gitProjectId, String projectName, String gitUser, String gitToken, String gitUrl, Date createTime, Date updateTime) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.gitProjectId = gitProjectId;
        this.projectName = projectName;
        this.gitUser = gitUser;
        this.gitToken = gitToken;
        this.gitUrl = gitUrl;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public GitProjectGitInfo(Long workspaceId, String gitProjectId, String projectName, String gitUser, String gitToken, String gitUrl) {
        this.workspaceId = workspaceId;
        this.gitProjectId = gitProjectId;
        this.projectName = projectName;
        this.gitUser = gitUser;
        this.gitToken = gitToken;
        this.gitUrl = gitUrl;
    }

    public GitProjectGitInfo(Long workspaceId, String projectName, String gitUser, String gitToken, String gitUrl) {
        this.workspaceId = workspaceId;
        this.projectName = projectName;
        this.gitUser = gitUser;
        this.gitToken = gitToken;
        this.gitUrl = gitUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getGitProjectId() {
        return gitProjectId;
    }

    public void setGitProjectId(String gitProjectId) {
        this.gitProjectId = gitProjectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getGitTokenEncrypt() {
        return gitTokenEncrypt;
    }

    public void setGitTokenEncrypt(String gitTokenEncrypt) {
        this.gitTokenEncrypt = gitTokenEncrypt;
    }
}
