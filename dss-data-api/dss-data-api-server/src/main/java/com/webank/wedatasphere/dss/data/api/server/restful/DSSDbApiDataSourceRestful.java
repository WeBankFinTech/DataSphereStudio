package com.webank.wedatasphere.dss.data.api.server.restful;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.webank.wedatasphere.dss.data.api.server.entity.DataSource;
import com.webank.wedatasphere.dss.data.api.server.service.ApiDataSourceService;
import com.webank.wedatasphere.dss.data.api.server.util.CryptoUtils;
import com.webank.wedatasphere.dss.data.api.server.util.JdbcUtil;
import com.webank.wedatasphere.dss.data.api.server.util.PoolManager;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
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

    @Resource
    private ApiDataSourceService dssDbApiDataSourceService;
    private static final Logger logger = LoggerFactory.getLogger(DSSDbApiDataSourceRestful.class);

    @GET
    @Path("/connections")
    public Message connect(@QueryParam("workspaceId") Integer workspaceId, @QueryParam("type") String type) {

        List<DataSource> allConnections = dssDbApiDataSourceService.getAllConnections(workspaceId, type);
        List<DataSource> availableConnections = dssDbApiDataSourceService.getAvailableConns(allConnections);
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

    @POST
    @Path("/add")
    public Message addDatasource(@RequestBody DataSource dataSource,@Context HttpServletRequest req){

        dataSource.setPwd(CryptoUtils.object2String(dataSource.getPwd()));
        dataSource.setCreateBy(SecurityFilter.getLoginUsername(req));
        dssDbApiDataSourceService.addDatasource(dataSource);
        return Message.ok("保存成功");
    }



}
