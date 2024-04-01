package com.webank.wedatasphere.dss.framework.workspace.dao;

import com.webank.wedatasphere.dss.framework.workspace.bean.GitUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DSSWorkspaceGitMapper {
    void insert(GitUserEntity gitUserDO);

    void update(GitUserEntity gitUserDO);

    GitUserEntity selectByWorkspaceId(@Param("workspaceId") Long workspaceId);
}
