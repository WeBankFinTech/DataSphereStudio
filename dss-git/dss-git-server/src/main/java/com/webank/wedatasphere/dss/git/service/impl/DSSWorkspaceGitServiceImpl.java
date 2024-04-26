package com.webank.wedatasphere.dss.git.service.impl;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserUpdateRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserInfoRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserUpdateResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserInfoResponse;
import com.webank.wedatasphere.dss.git.dao.DSSWorkspaceGitMapper;
import com.webank.wedatasphere.dss.git.service.DSSWorkspaceGitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DSSWorkspaceGitServiceImpl implements DSSWorkspaceGitService {

    @Autowired
    private DSSWorkspaceGitMapper workspaceGitMapper;


    @Override
    public GitUserUpdateResponse associateGit(GitUserUpdateRequest gitUserCreateRequest) throws DSSErrorException {
        if (gitUserCreateRequest == null) {
            throw  new DSSErrorException(010101, "gitUserCreateRequest is null");
        }
        GitUserEntity gitUser = gitUserCreateRequest.getGitUser();
        String userName = gitUserCreateRequest.getUsername();
        // 不存在则更新，存在则新增
        GitUserEntity oldGitUserDo = selectGit(gitUser.getWorkspaceId(), gitUser.getType());
        gitUser.setUpdateBy(userName);
        if (oldGitUserDo == null) {
            gitUser.setCreateBy(userName);
            workspaceGitMapper.insert(gitUser);
        }else {
            workspaceGitMapper.update(gitUser);
        }

        return new GitUserUpdateResponse();
    }


    @Override
    public GitUserInfoResponse selectGitUserInfo(GitUserInfoRequest gitUserInfoRequest) throws DSSErrorException {
        if (gitUserInfoRequest == null) {
            throw  new DSSErrorException(010101, "gitUserCreateRequest is null");
        }
        Long workspaceId = gitUserInfoRequest.getWorkspaceId();
        String type = gitUserInfoRequest.getType();
        GitUserEntity gitUserEntity = selectGit(workspaceId, type);

        GitUserInfoResponse gitUserInfoResponse = new GitUserInfoResponse();
        gitUserInfoResponse.setGitUser(gitUserEntity);
        return gitUserInfoResponse;
    }

    @Override
    public GitUserEntity selectGit(Long workspaceId, String type) {
        return workspaceGitMapper.selectByWorkspaceId(workspaceId, type);
    }

}
