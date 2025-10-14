package com.webank.wedatasphere.dss.git.common.protocol.response;

import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;

import java.util.List;

public class GitUserInfoListResponse {
    private List<GitUserEntity> gitUserEntities;

    public List<GitUserEntity> getGitUserEntities() {
        return gitUserEntities;
    }

    public void setGitUserEntities(List<GitUserEntity> gitUserEntities) {
        this.gitUserEntities = gitUserEntities;
    }
}
