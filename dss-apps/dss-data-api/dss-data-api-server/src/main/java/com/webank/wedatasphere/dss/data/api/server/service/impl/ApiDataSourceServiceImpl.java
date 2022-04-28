package com.webank.wedatasphere.dss.data.api.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.data.api.server.dao.DataSourceMapper;
import com.webank.wedatasphere.dss.data.api.server.entity.DataSource;
import com.webank.wedatasphere.dss.data.api.server.util.CryptoUtils;
import com.webank.wedatasphere.dss.data.api.server.service.ApiDataSourceService;
import com.webank.wedatasphere.dss.data.api.server.util.JdbcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Service
public class ApiDataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements ApiDataSourceService {
    @Resource
    private DataSourceMapper dataSourceMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDataSourceServiceImpl.class);

    @Override
    public DataSource selectById(Integer datasourceId) {

        return dataSourceMapper.selectById(datasourceId);
    }

    @Override
    public List<DataSource> getAllConnections(Integer workspaceId, String type) {
        DataSource dataSource = new DataSource();
        dataSource.setWorkspaceId(workspaceId);
        dataSource.setType(type);
        return dataSourceMapper.selectByTypeAndWorkspaceId(dataSource);
    }

    @Override
    public List<DataSource> getAvailableConns(List<DataSource> allConnections) {

        Connection connection = null;
        List<DataSource> availableConns = new ArrayList<DataSource>();
        for (DataSource datasource : allConnections) {
            try {
                datasource.setPwd(CryptoUtils.string2Object(datasource.getPwd()).toString());
                connection = JdbcUtil.getConnection(datasource);
                datasource.setPwd("***");
                availableConns.add(datasource);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        LOGGER.info(e.getMessage());
                    }
                }
            }

        }
        return availableConns;

    }

    @Override
    public void addDatasource(DataSource dataSource) {
        dataSourceMapper.addDatasource(dataSource);
    }

    @Override
    public List<DataSource> listAllDatasources(DataSource dataSource) {
        return dataSourceMapper.listAllDatasources(dataSource);
    }

    @Override
    public void deleteById(Integer id) {
        dataSourceMapper.deleteById(id);
    }

    @Override
    public void editDatasource(DataSource dataSource) {
        dataSourceMapper.editDatasource(dataSource);
    }

    @Override
    public boolean isDataSourceUsing(Integer id) {
        if (dataSourceMapper.dataSourceUsingCount(id) > 0) {
            return true;
        }
        return false;
    }

}
