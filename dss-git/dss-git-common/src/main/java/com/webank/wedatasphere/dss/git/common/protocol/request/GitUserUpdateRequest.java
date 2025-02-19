package com.webank.wedatasphere.dss.git.common.protocol.request;

import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;


public class GitUserUpdateRequest {
    private GitUserEntity gitUser;
    private String username;

    public GitUserEntity getGitUser() {
        return gitUser;
    }

    public void setGitUser(GitUserEntity gitUser) {
        this.gitUser = gitUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
