package com.webank.wedatasphere.dss.git.dao;

import com.webank.wedatasphere.dss.git.common.protocol.GitUserEntity;

import com.webank.wedatasphere.dss.git.dto.GitProjectGitInfo;
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

    List<GitProjectGitInfo> getProjectInfoByWorkspaceId(@Param ("workspaceId") Long workspaceId);

    GitProjectGitInfo getProjectInfoByProjectName(@Param ("projectName") String projectName);

    void insertProjectInfo(GitProjectGitInfo projectGitInfo);

    void updateProjectToken(@Param("projectName") String projectName, @Param("gitToken") String gitToken);

    void updateProjectId(@Param("projectName") String projectName, @Param("gitProjectId") String gitProjectId);
}
