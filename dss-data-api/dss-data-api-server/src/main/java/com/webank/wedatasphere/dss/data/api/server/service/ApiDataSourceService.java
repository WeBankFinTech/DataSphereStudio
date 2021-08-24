package com.webank.wedatasphere.dss.data.api.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.data.api.server.entity.DataSource;

import java.util.List;

public interface ApiDataSourceService extends IService<DataSource> {

    DataSource selectById(Integer datasourceId);

    List<DataSource> getAllConnections(Integer workspaceId, String type);


    List<String> getAvailableConnNames(List<DataSource> allConnections);

    List<DataSource> getAvailableConns(List<DataSource> allConnections);

    void addDatasource(DataSource dataSource);
}
