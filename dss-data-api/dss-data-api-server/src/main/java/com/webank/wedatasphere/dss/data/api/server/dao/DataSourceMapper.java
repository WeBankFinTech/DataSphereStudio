package com.webank.wedatasphere.dss.data.api.server.dao;

import com.webank.wedatasphere.dss.data.api.server.entity.DataSource;

import java.util.List;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface DataSourceMapper extends BaseMapper<DataSource> {

    List<DataSource> selectByTypeAndWorkspaceId(DataSource dataSource);

    DataSource selectById(Integer datasourceId);

    void addDatasource(DataSource dataSource);

    List<DataSource> listAllDatasources(DataSource dataSource);

    void deleteById(Integer id);

    void editDatasource(DataSource dataSource);

    int dataSourceUsingCount(Integer id);
}
