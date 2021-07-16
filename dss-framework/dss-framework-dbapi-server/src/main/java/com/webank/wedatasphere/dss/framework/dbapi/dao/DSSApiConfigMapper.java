package com.webank.wedatasphere.dss.framework.dbapi.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiConfig;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiGroup;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiListInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface DSSApiConfigMapper extends BaseMapper<ApiConfig> {

    @Insert("insert into dss_dataapi_group(workspace_id, name, note, create_by) " +
            "values(#{apiGroup.workspaceId}, #{apiGroup.name}, #{apiGroup.note}, #{apiGroup.createBy})")
    @Options(useGeneratedKeys = true, keyProperty = "apiGroup.id", keyColumn = "id")
    int addApiGroup(@Param("apiGroup") ApiGroup apiGroup);



    @Select("select id,name from dss_dataapi_group where workspace_id = #{workspaceId}")
    @Results({
            @Result(property = "groupId", column = "id"),
            @Result(property = "groupName", column = "name"),

    })
    List<ApiGroupInfo> getGroupByWorkspaceId(@Param("workspaceId") String workspaceId);


    @Select("select id,api_name as name from dss_dataapi_config where group_id = #{groupId}")
    List<ApiListInfo> getApiListByGroup(@Param("groupId") int groupId);
}
