package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.webank.wedatasphere.dss.framework.workspace.bean.GitUserEntity;
import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceGitMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceGitService;
import org.springframework.beans.factory.annotation.Autowired;

public class DSSWorkspaceGitServiceImpl implements DSSWorkspaceGitService {

    @Autowired
    private DSSWorkspaceGitMapper workspaceGitMapper;


    @Override
    public void associateGit(GitUserEntity gitUser, String userName) {
        // 不存在则更新，存在则新增
        GitUserEntity oldGitUserDo = selectGit(gitUser.getWorkspaceId());
        gitUser.setUpdateBy(userName);
        if (oldGitUserDo == null) {
            gitUser.setCreateBy(userName);
            workspaceGitMapper.insert(gitUser);
        }else {
            workspaceGitMapper.update(gitUser);
        }
    }

    @Override
    public GitUserEntity selectGit(Long workspaceId) {
        return workspaceGitMapper.selectByWorkspaceId(workspaceId);
    }
}
