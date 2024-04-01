package com.webank.wedatasphere.dss.framework.workspace.bean;


import java.util.Date;

public class GitUserEntity {
    private Long id;
    private Long workspaceId;
    private String gitUser;
    private String gitToken;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;

    public GitUserEntity() {
    }

    public GitUserEntity(Long workspaceId, String gitUser, String gitToken, String updateBy) {
        this.workspaceId = workspaceId;
        this.gitUser = gitUser;
        this.gitToken = gitToken;
        this.updateBy = updateBy;
    }

    public GitUserEntity(Long workspaceId, String gitUser, String gitToken, String createBy, String updateBy) {
        this.workspaceId = workspaceId;
        this.gitUser = gitUser;
        this.gitToken = gitToken;
        this.createBy = createBy;
        this.updateBy = updateBy;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
}
