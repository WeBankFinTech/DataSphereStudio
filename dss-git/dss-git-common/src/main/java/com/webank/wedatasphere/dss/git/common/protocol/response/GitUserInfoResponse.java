package com.webank.wedatasphere.dss.git.common.protocol.response;

import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;


public class GitUserInfoResponse {
    private GitUserEntity gitUser;

    public GitUserEntity getGitUser() {
        return gitUser;
    }

    public void setGitUser(GitUserEntity gitUser) {
        this.gitUser = gitUser;
    }
}
