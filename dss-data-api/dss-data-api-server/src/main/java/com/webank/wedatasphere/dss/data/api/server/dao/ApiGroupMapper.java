package com.webank.wedatasphere.dss.data.api.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.data.api.server.entity.ApiGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApiGroupMapper extends BaseMapper<ApiGroup> {

    @Select("SELECT * FROM dss_dataapi_group WHERE workspace_id = #{workspaceId}")
    List<ApiGroup> getApiGroupList(Long workspaceId);
}
