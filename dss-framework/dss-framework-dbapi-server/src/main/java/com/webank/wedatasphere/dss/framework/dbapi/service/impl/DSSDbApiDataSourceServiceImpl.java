package com.webank.wedatasphere.dss.framework.dbapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.framework.dbapi.dao.DataSourceMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.DataSource;
import com.webank.wedatasphere.dss.framework.dbapi.service.DSSDbApiDataSourceService;
import com.webank.wedatasphere.dss.framework.dbapi.util.JdbcUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class DSSDbApiDataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements DSSDbApiDataSourceService {
    @Resource
    private DataSourceMapper dataSourceMapper;
    private Logger logger;

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
    public List<String> getAvailableConnNames(List<DataSource> allConnections) {

        Connection connection = null;
        List<String> availableConns = new ArrayList<String>();
        for (DataSource datasource : allConnections) {
            try {
                connection = JdbcUtil.getConnection(datasource);
                availableConns.add(datasource.getName());
            } catch (Exception e) {
                logger.warning(e.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        logger.info(e.getMessage());
                    }
                }
            }

        }
        return availableConns;
    }
}
