package com.webank.wedatasphere.dss.git.dao;

import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DSSWorkspaceGitMapper {
    void insert(GitUserEntity gitUserDO);

    void update(GitUserEntity gitUserDO);

    GitUserEntity selectByWorkspaceId(@Param("workspaceId") Long workspaceId, @Param("type") String type);
}
