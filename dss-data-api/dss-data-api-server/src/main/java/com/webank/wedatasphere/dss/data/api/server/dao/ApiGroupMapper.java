package com.webank.wedatasphere.dss.data.api.server.dao;

import com.webank.wedatasphere.dss.data.api.server.entity.ApiGroup;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/** @Classname ApiGroupMapper @Description TODO @Date 2021/7/23 15:44 @Created by suyc */
@Mapper
public interface ApiGroupMapper extends BaseMapper<ApiGroup> {

    @Select("SELECT * FROM dss_dataapi_group WHERE workspace_id = #{workspaceId}")
    List<ApiGroup> getApiGroupList(Long workspaceId);
}
