package com.webank.wedatasphere.dss.data.api.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.data.api.server.entity.ApiConfig;
import com.webank.wedatasphere.dss.data.api.server.entity.ApiGroup;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiInfo;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiListInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ApiConfigMapper extends BaseMapper<ApiConfig> {

    public Boolean release(@Param("status") Integer status, @Param("apiId") String apiId);

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


    @Select("select id,api_name as name,req_fields,api_path as path from dss_dataapi_config where group_id = #{groupId}")
    List<ApiListInfo> getApiListByGroup(@Param("groupId") int groupId);


    List<ApiInfo> getApiInfoList(@Param("workspaceId") Long workspaceId, @Param("apiName") String apiName);
    List<ApiInfo> getOnlineApiInfoList(@Param("workspaceId") Long workspaceId, @Param("apiName") String apiName);

    @Update("UPDATE dss_dataapi_config SET `status` = 0 WHERE `id` = #{apiId}")
    void offlineApi(@Param("apiId") Long apiId);

    @Update("UPDATE dss_dataapi_config SET `status` = 1 WHERE `id` = #{apiId}")
    void onlineApi(@Param("apiId") Long apiId);

    @Select("SELECT COUNT(1) FROM dss_dataapi_config WHERE `is_delete` = 0 AND `status` = 1 AND `workspace_id` = #{workspaceId}")
    Long getOnlineApiCnt(Long workspaceId);

    @Select("SELECT COUNT(1) FROM dss_dataapi_config WHERE `is_delete` = 0 AND `status` = 0 AND `workspace_id` = #{workspaceId}")
    Long getOfflineApiCnt(Long workspaceId);

    ApiInfo getApiInfo(Long apiId);

    @Update("UPDATE dss_dataapi_config SET `is_test` = #{isTest} WHERE `id` = #{apiId}")
    void updateApiTestStatus(@Param("apiId") int apiId,@Param("isTest") int isTest);
}
