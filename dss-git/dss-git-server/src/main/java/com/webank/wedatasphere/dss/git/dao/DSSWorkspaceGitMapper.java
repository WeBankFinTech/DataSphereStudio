package com.webank.wedatasphere.dss.git.dao;

import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DSSWorkspaceGitMapper {
    void insert(GitUserEntity gitUserDO);

    void update(GitUserEntity gitUserDO);

    GitUserEntity selectByWorkspaceId(@Param("workspaceId") Long workspaceId, @Param("type") String type);

    GitUserEntity selectByUser(@Param("gitUser") String gitUser);

    List<GitUserEntity> selectGitUser(@Param("workspaceId") Long workspaceId, @Param("type") String type, @Param("gitUser") String gitUser);

    List<Long> getAllWorkspaceId();
}
