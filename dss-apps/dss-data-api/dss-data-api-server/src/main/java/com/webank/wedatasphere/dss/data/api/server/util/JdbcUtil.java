package com.webank.wedatasphere.dss.data.api.server.util;

import com.alibaba.druid.util.JdbcConstants;
import com.webank.wedatasphere.dss.data.api.server.entity.DataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class JdbcUtil {

    public static ResultSet query(String sql, Connection connection) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet;
        }
    }

    public static Connection getConnection(DataSource ds) throws SQLException, ClassNotFoundException {
        String url = ds.getUrl();
        switch (ds.getType()) {
            case JdbcConstants.MYSQL:
                Class.forName(JdbcConstants.MYSQL_DRIVER);
                break;
            case JdbcConstants.POSTGRESQL:
                Class.forName(JdbcConstants.POSTGRESQL_DRIVER);
                break;
            case JdbcConstants.HIVE:
                Class.forName(JdbcConstants.HIVE_DRIVER);
                break;
            case JdbcConstants.SQL_SERVER:
                Class.forName(JdbcConstants.SQL_SERVER_DRIVER_SQLJDBC4);
                break;
            case JdbcConstants.CLICKHOUSE:
                Class.forName(JdbcConstants.CLICKHOUSE_DRIVER);
                break;
            case JdbcConstants.KYLIN:
                Class.forName(JdbcConstants.KYLIN_DRIVER);
                break;
            case JdbcConstants.ORACLE:
                Class.forName(JdbcConstants.ORACLE_DRIVER);
                break;
            default:
                break;
        }

        Connection connection = DriverManager.getConnection(url, ds.getUsername(), ds.getPwd());
        log.info("获取连接成功");
        return connection;
    }

    /**
     * 查询库中所有表
     * @param conn
     * @param type
     * @return
     */
    public static List<String> getAllTables(Connection conn, String type) {
        List<String> list = new ArrayList<>();
        PreparedStatement pst = null;
        try {
            String sql;
            switch (type) {
                case "MYSQL":
                case "HIVE":
                    sql = "show tables";
                    break;
                case "POSTGRESQL":
                    sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name";
                    break;
                // TODO
                case "SQLSERVER":
                    sql = "select * from sys.tables";
                    break;
                default:
                    sql = "show tables";

            }

            pst = conn.prepareStatement(sql);
            ResultSet resultSet = pst.executeQuery();

            while (resultSet.next()) {
                String s = resultSet.getString(1);
                list.add(s);
            }
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (pst != null)
                    pst.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询表所有字段
     * @param conn
     * @param type
     * @param table
     * @return
     */
    public static List<HashMap> getRDBMSColumnProperties(Connection conn, String type, String table) {
        List<HashMap> list = new ArrayList<>();

        ResultSet resultSet = null;
        try {
            String sql;
            switch (type) {
                case "POSTGRESQL":
                    sql = "select * from \"" + table + "\" where 1=2";
                    break;
                default:
                    sql = "show full columns from " + table;
            }
            log.info(sql);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    HashMap<String, String> colProp = new HashMap<>();
                    colProp.put("Comment", resultSet.getString("Comment"));
                    colProp.put("fieldType", resultSet.getString("Type"));
                    colProp.put("columnName", resultSet.getString("Field"));
                    list.add(colProp);
                }
            }
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}