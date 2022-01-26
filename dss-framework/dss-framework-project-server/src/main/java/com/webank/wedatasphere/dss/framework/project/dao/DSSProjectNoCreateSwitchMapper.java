package com.webank.wedatasphere.dss.framework.project.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Description 工程检查开关类
 */
@Mapper
public interface DSSProjectNoCreateSwitchMapper {
    @Select("select count(0) from dss_project_no_create_switch where appconn_instance_id = #{appconnInstanceId}")
    Long getCountByAppconnInstanceId(@Param("appconnInstanceId")Long appconnInstanceId);
}
