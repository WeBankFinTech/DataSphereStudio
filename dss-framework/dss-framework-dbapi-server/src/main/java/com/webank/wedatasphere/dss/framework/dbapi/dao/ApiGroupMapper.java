package com.webank.wedatasphere.dss.framework.dbapi.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Classname ApiGroupMapper
 * @Description TODO
 * @Date 2021/7/23 15:44
 * @Created by suyc
 */
@Mapper
public interface ApiGroupMapper extends BaseMapper<ApiGroup> {

    @Select("SELECT * FROM dss_dataapi_group WHERE workspace_id = #{workspaceId}")
    List<ApiGroup> getApiGroupList(Long workspaceId);
}
