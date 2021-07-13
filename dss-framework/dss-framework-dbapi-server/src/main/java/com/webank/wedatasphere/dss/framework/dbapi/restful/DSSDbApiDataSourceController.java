package com.webank.wedatasphere.dss.framework.dbapi.restful;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.webank.wedatasphere.dss.framework.dbapi.entity.DataSource;
import com.webank.wedatasphere.dss.framework.dbapi.dao.DataSourceMapper;
import com.webank.wedatasphere.dss.framework.dbapi.util.JdbcUtil;
import com.webank.wedatasphere.dss.framework.dbapi.util.PoolManager;
import com.webank.wedatasphere.linkis.server.Message;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author lvjw
 */
@Component
@Path("/dss/framework/dbapi/datasource")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSDbApiDataSourceController {
    DataSourceMapper dataSourceMapper;
    private static final Logger logger = LoggerFactory.getLogger(DSSDbApiDataSourceController.class);

    @GET
    @Path("/connections")
    public Message connect(@QueryParam("workspaceId") Integer workspaceId, @QueryParam("type") String type) {
        DataSource ds = new DataSource();
        ds.setWorkspaceId(workspaceId);
        ds.setType(type);
        DataSource dataSource = dataSourceMapper.selectByTypeAndWorkspaceId(ds);
        Connection connection = null;

        try {
            connection = JdbcUtil.getConnection(dataSource);
            return Message.ok();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Message.error(e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }


    @GET
    @Path("/tables")

    public Message getAllTables(@QueryParam("datasourceId") Integer datasourceId) throws SQLException {

        DataSource dataSource = dataSourceMapper.selectById(datasourceId);
        DruidPooledConnection connection = PoolManager.getPooledConnection(dataSource);
        List<String> tables = JdbcUtil.getAllTables(connection, dataSource.getType());
        return Message.ok().data("allTables",tables) ;
    }

    @GET
    @Path("/cols")
    public Message getAllCols(@QueryParam("datasourceId") Integer datasourceId, @QueryParam("tableName") String tableName) throws SQLException {
        DataSource dataSource = dataSourceMapper.selectById(datasourceId);
        DruidPooledConnection connection = PoolManager.getPooledConnection(dataSource);
        List<JSONObject> columns = JdbcUtil.getRDBMSColumnProperties(connection, dataSource.getType(), tableName);
        return Message.ok().data("allCols", columns);
    }


}
