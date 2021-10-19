package com.webank.wedatasphere.dss.data.api.server.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.data.api.server.entity.DataSource;

import java.util.List;

public interface DataSourceMapper extends BaseMapper<DataSource> {

    List<DataSource> selectByTypeAndWorkspaceId(DataSource dataSource);

    DataSource selectById(Integer datasourceId);

    void addDatasource(DataSource dataSource);

    List<DataSource> listAllDatasources(DataSource dataSource);

    void deleteById(Integer id);

    void editDatasource(DataSource dataSource);

    int dataSourceUsingCount(Integer id);
}
