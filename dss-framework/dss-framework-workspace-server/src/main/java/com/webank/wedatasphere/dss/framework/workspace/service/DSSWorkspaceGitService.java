package com.webank.wedatasphere.dss.framework.workspace.service;

import com.webank.wedatasphere.dss.framework.workspace.bean.GitUserEntity;

public interface DSSWorkspaceGitService {

    void associateGit(GitUserEntity gitUser, String userName);

    GitUserEntity selectGit(Long workspaceId);
}
