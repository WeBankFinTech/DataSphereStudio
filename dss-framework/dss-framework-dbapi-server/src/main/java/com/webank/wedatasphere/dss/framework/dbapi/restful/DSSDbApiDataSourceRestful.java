package com.webank.wedatasphere.dss.framework.dbapi.restful;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.webank.wedatasphere.dss.framework.dbapi.entity.DataSource;
import com.webank.wedatasphere.dss.framework.dbapi.dao.DataSourceMapper;
import com.webank.wedatasphere.dss.framework.dbapi.service.DSSDbApiDataSourceService;
import com.webank.wedatasphere.dss.framework.dbapi.util.JdbcUtil;
import com.webank.wedatasphere.dss.framework.dbapi.util.PoolManager;
import com.webank.wedatasphere.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * @author lvjw
 */
@Component
@Path("/dss/framework/dbapi/datasource")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSDbApiDataSourceRestful {
    private DataSourceMapper dataSourceMapper;
    @Resource
    private DSSDbApiDataSourceService dssDbApiDataSourceService;
    private static final Logger logger = LoggerFactory.getLogger(DSSDbApiDataSourceRestful.class);

    @GET
    @Path("/connections")
    public Message connect(@QueryParam("workspaceId") Integer workspaceId, @QueryParam("type") String type) {


        List<DataSource> availableConnections = dssDbApiDataSourceService.getAvailableConns(dssDbApiDataSourceService.getAllConnections(workspaceId, type));
        return Message.ok().data("availableConns", availableConnections);

    }


    @GET
    @Path("/tables")

    public Message getAllTables(@QueryParam("datasourceId") Integer datasourceId) throws SQLException {

        DataSource dataSource = dssDbApiDataSourceService.selectById(datasourceId);
        DruidPooledConnection connection = PoolManager.getPooledConnection(dataSource);
        List<String> tables = JdbcUtil.getAllTables(connection, dataSource.getType());
        return Message.ok().data("allTables", tables);
    }

    @GET
    @Path("/cols")
    public Message getAllCols(@QueryParam("datasourceId") Integer datasourceId, @QueryParam("tableName") String tableName) throws SQLException {
        DataSource dataSource = dssDbApiDataSourceService.selectById(datasourceId);
        DruidPooledConnection connection = PoolManager.getPooledConnection(dataSource);
        List<HashMap> columns = JdbcUtil.getRDBMSColumnProperties(connection, dataSource.getType(), tableName);
        return Message.ok().data("allCols", columns);
    }


}
