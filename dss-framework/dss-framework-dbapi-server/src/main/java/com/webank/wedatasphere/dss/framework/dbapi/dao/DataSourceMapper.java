package com.webank.wedatasphere.dss.framework.dbapi.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.DataSource;

public interface DataSourceMapper extends BaseMapper<DataSource> {

    DataSource selectByTypeAndWorkspaceId(DataSource dataSource);
}
