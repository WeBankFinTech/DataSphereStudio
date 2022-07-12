package com.webank.wedatasphere.dss.data.api.server.restful;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.webank.wedatasphere.dss.data.api.server.entity.DataSource;
import com.webank.wedatasphere.dss.data.api.server.service.ApiDataSourceService;
import com.webank.wedatasphere.dss.data.api.server.util.CryptoUtils;
import com.webank.wedatasphere.dss.data.api.server.util.JdbcUtil;
import com.webank.wedatasphere.dss.data.api.server.util.PoolManager;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = "/dss/data/api/datasource", produces = {"application/json"})
public class DSSDbApiDataSourceRestful {

    @Resource
    private ApiDataSourceService dssDbApiDataSourceService;
    private static final Logger logger = LoggerFactory.getLogger(DSSDbApiDataSourceRestful.class);

    @RequestMapping(path = "connections", method = RequestMethod.GET)
    public Message connect(HttpServletRequest httpServletRequest, @RequestParam(value = "workspaceId", required = false) Integer workspaceId,
                           @RequestParam("type") String type) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        List<DataSource> allConnections = dssDbApiDataSourceService.getAllConnections(Integer.valueOf(workspace.getWorkspaceName()), type);
        return Message.ok().data("availableConns", allConnections);

    }


    @RequestMapping(path = "tables", method = RequestMethod.GET)
    public Message getAllTables(@RequestParam("datasourceId") Integer datasourceId) throws SQLException {

        DataSource dataSource = dssDbApiDataSourceService.selectById(datasourceId);
        DruidPooledConnection connection = PoolManager.getPooledConnection(dataSource);
        List<String> tables = JdbcUtil.getAllTables(connection, dataSource.getType());
        return Message.ok().data("allTables", tables);
    }


    @RequestMapping(path = "cols", method = RequestMethod.GET)
    public Message getAllCols(@RequestParam("datasourceId") Integer datasourceId, @RequestParam("tableName") String tableName) throws SQLException {
        DataSource dataSource = dssDbApiDataSourceService.selectById(datasourceId);
        DruidPooledConnection connection = PoolManager.getPooledConnection(dataSource);
        List<HashMap> columns = JdbcUtil.getRDBMSColumnProperties(connection, dataSource.getType(), tableName);
        return Message.ok().data("allCols", columns);
    }


    @RequestMapping(path = "add", method = RequestMethod.POST)
    public Message addDatasource(@RequestBody DataSource dataSource, HttpServletRequest req) {

        dataSource.setPwd(CryptoUtils.object2String(dataSource.getPwd()));
        dataSource.setCreateBy(SecurityFilter.getLoginUsername(req));
        dssDbApiDataSourceService.addDatasource(dataSource);
        return Message.ok("保存成功");
    }


    @RequestMapping(path = "list", method = RequestMethod.GET)
    public Message getAllDs(HttpServletRequest httpServletRequest,
                            @RequestParam(value = "workspaceId", required = false) Integer workspaceId,
                            @RequestParam("type") String type, @RequestParam("name") String name) {
        Workspace workspace = SSOHelper.getWorkspace(httpServletRequest);
        DataSource dataSource = new DataSource();
        dataSource.setWorkspaceId(Integer.valueOf(workspace.getWorkspaceName()));
        dataSource.setType(type);
        dataSource.setName(name);
        List<DataSource> allDatasource = dssDbApiDataSourceService.listAllDatasources(dataSource);

        return Message.ok().data("allDs", allDatasource);
    }

    @RequestMapping(path = "edit", method = RequestMethod.POST)
    public Message editDatasource(@RequestBody DataSource dataSource, HttpServletRequest req) {
        if (StringUtils.isNotEmpty(dataSource.getPwd())) {
            PoolManager.removeJdbcConnectionPool(dataSource.getDatasourceId());
            dataSource.setPwd(CryptoUtils.object2String(dataSource.getPwd()));
            dataSource.setUpdateBy(SecurityFilter.getLoginUsername(req));
            dssDbApiDataSourceService.editDatasource(dataSource);
            return Message.ok("修改成功");
        }
        return Message.error("密码不能为空");
    }


    @RequestMapping(path = "/delete/{id}", method = RequestMethod.POST)
    public Message deleteDatasource(@PathVariable("id") Integer id) {
        if (dssDbApiDataSourceService.isDataSourceUsing(id)) {
            return Message.error("该数据源正在被使用,请下线与此数据源相关的api后再删除");
        } else {
            dssDbApiDataSourceService.deleteById(id);
            return Message.ok("删除成功");
        }
    }


    @RequestMapping(path = "test", method = RequestMethod.POST)
    public Message testDatasource(@RequestBody DataSource dataSource) {
        Connection connection = null;
        try {
            connection = JdbcUtil.getConnection(dataSource);
        } catch (Exception e) {
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
        return Message.ok("测试连接成功");
    }

}
