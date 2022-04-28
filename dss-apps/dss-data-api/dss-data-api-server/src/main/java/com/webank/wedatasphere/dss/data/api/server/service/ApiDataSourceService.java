package com.webank.wedatasphere.dss.data.api.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.data.api.server.entity.DataSource;

import java.util.List;

public interface ApiDataSourceService extends IService<DataSource> {

    DataSource selectById(Integer datasourceId);

    List<DataSource> getAllConnections(Integer workspaceId, String type);



    List<DataSource> getAvailableConns(List<DataSource> allConnections);

    void addDatasource(DataSource dataSource);


    List<DataSource> listAllDatasources(DataSource dataSource);

    void deleteById(Integer datasourceId);

    void editDatasource(DataSource dataSource);

    boolean isDataSourceUsing(Integer id);
}
