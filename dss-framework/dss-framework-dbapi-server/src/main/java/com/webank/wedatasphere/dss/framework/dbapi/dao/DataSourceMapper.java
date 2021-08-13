package com.webank.wedatasphere.dss.framework.dbapi.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.DataSource;

import java.util.List;

public interface DataSourceMapper extends BaseMapper<DataSource> {

    List<DataSource> selectByTypeAndWorkspaceId(DataSource dataSource);

    DataSource selectById(Integer datasourceId);
}
