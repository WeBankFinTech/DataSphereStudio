package com.webank.wedatasphere.dss.data.asset.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import java.util.List;

/**
 * @ClassName: workspaceInfoMapper
 * @Description: 工作空间下用户信息
 * @author: lijw
 * @date: 2021/9/3 10:43
 */
@Mapper
public interface WorkspaceInfoMapper {

    @Select("select username from dss_workspace_user where workspace_id= #{workspaceId} and username like  #{search} ")
    List<String>  getWorkspaceUsersName (@Param("workspaceId") int workspaceId,@Param("search") String search);

}
