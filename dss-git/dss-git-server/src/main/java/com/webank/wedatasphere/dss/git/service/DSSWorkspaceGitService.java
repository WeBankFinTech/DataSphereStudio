package com.webank.wedatasphere.dss.git.service;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserUpdateRequest;
import com.webank.wedatasphere.dss.git.common.protocol.request.GitUserInfoRequest;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserUpdateResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitUserInfoResponse;

public interface DSSWorkspaceGitService {

    GitUserUpdateResponse associateGit(GitUserUpdateRequest gitUserCreateRequest) throws DSSErrorException;

    GitUserInfoResponse selectGitUserInfo(GitUserInfoRequest gitUserInfoRequest) throws DSSErrorException;

    GitUserEntity selectGit(Long workspaceId, String type);
}
