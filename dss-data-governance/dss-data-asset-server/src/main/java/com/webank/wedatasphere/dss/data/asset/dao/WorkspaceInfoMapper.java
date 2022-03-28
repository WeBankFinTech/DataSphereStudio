package com.webank.wedatasphere.dss.data.asset.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import java.util.List;

@Mapper
public interface WorkspaceInfoMapper {

    @Select("select distinct username from dss_workspace_user_role where workspace_id= #{workspaceId} and username like  #{search} ")
    List<String>  getWorkspaceUsersName (@Param("workspaceId") int workspaceId,@Param("search") String search);

}
