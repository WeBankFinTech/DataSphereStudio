package com.webank.wedatasphere.dss.framework.dbapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.framework.dbapi.entity.DataSource;

import java.util.List;

public interface ApiDataSourceService extends IService<DataSource> {

    DataSource selectById(Integer datasourceId);

    List<DataSource> getAllConnections(Integer workspaceId, String type);


    List<String> getAvailableConnNames(List<DataSource> allConnections);

    List<DataSource> getAvailableConns(List<DataSource> allConnections);
}
