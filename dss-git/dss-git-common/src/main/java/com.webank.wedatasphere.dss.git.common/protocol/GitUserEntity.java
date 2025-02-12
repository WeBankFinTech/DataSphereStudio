package com.webank.wedatasphere.dss.git.common.protocol;


import java.util.Date;

public class GitUserEntity {
    private String gitUser;
    private String gitToken;

    public GitUserEntity() {
    }

    public GitUserEntity(String gitUser, String gitToken) {
        this.gitUser = gitUser;
        this.gitToken = gitToken;
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
