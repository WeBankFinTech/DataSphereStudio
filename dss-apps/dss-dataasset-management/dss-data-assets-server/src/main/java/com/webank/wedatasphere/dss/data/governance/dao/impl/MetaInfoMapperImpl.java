package com.webank.wedatasphere.dss.data.governance.dao.impl;

import com.webank.wedatasphere.dss.data.governance.dao.MetaInfoMapper;
import com.webank.wedatasphere.dss.data.governance.entity.PartInfo;
import com.webank.wedatasphere.dss.data.governance.entity.TableInfo;
import com.webank.wedatasphere.dss.data.governance.exception.DAOException;
import com.webank.wedatasphere.dss.data.governance.utils.DataSourceUtil;
import com.webank.wedatasphere.dss.data.governance.utils.DateUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class MetaInfoMapperImpl implements MetaInfoMapper {
    @Override
    public Long getTableStorage() throws DAOException {
        DataSource dataSource = DataSourceUtil.getDataSource();

        long num = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = dataSource.getConnection()) {
            String sql = "select SUM(PARAM_VALUE) from TABLE_PARAMS WHERE PARAM_KEY='totalSize'";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                num = rs.getLong(1);
            }
            String sql2 = "select SUM(PARAM_VALUE) from PARTITION_PARAMS WHERE PARAM_KEY='totalSize'";
            ps = con.prepareStatement(sql2);
            rs = ps.executeQuery();
            while (rs.next()) {
                num = num + rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DAOException(23001, e.getMessage());
        }
        return num;
    }

    @Override
    public List<TableInfo> getTop10Table() throws DAOException {
        DataSource dataSource = DataSourceUtil.getDataSource();

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TableInfo> tableInfos = new ArrayList<>();
        try (Connection con = dataSource.getConnection();) {
            String sql = "select DBS.NAME ,TBLS.TBL_NAME,TABLE_PARAMS.PARAM_VALUE as totalSize from DBS, TBLS,TABLE_PARAMS where TBLS.TBL_ID=TABLE_PARAMS.TBL_ID AND TBLS.DB_ID=DBS.DB_ID AND TABLE_PARAMS.PARAM_KEY='totalSize'  order by totalSize DESC limit 10";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                TableInfo tableinfo = new TableInfo();
                tableinfo.setTableName(rs.getString(1) + "." + rs.getString(2));
                tableinfo.setStorage(rs.getString(3));
                tableInfos.add(tableinfo);
            }
            String sql2 = "select DBS.NAME ,TBLS.TBL_NAME,SUM(PARTITION_PARAMS.PARAM_VALUE) as totalSize  from DBS,TBLS,PARTITIONS ,PARTITION_PARAMS where DBS.DB_ID=TBLS.DB_ID AND TBLS.TBL_ID=PARTITIONS.TBL_ID AND  PARTITIONS.PART_ID =PARTITION_PARAMS.PART_ID  AND PARTITION_PARAMS.PARAM_KEY='totalSize'  group by TBLS.TBL_NAME  order by totalSize  desc limit 10";
            ps = con.prepareStatement(sql2);
            rs = ps.executeQuery();
            while (rs.next()) {
                TableInfo tableinfo = new TableInfo();
                tableinfo.setTableName(rs.getString(1) + "." + rs.getString(2));
                tableinfo.setStorage(rs.getString(3));
                tableInfos.add(tableinfo);
            }

            tableInfos.sort((o1, o2) -> {
                long result = Long.parseLong(o2.getStorage()) - Long.parseLong(o1.getStorage());
                return Long.compare(result, 0L);
            });
        } catch (SQLException e) {
            throw new DAOException(23001, e.getMessage());
        }

        return tableInfos.stream().limit(10).collect(Collectors.toList());
    }

    @Override
    public int getTableInfo(String dbName, String tableName, Boolean isPartTable) throws DAOException {
        DataSource dataSource = DataSourceUtil.getDataSource();
        PreparedStatement ps = null;
        ResultSet rs = null;
        int res = 0;
        try (Connection con = dataSource.getConnection()) {
            String sql = null;
            if (isPartTable == false) {
                sql = "select TABLE_PARAMS.PARAM_VALUE as totalSize from DBS, TBLS,TABLE_PARAMS where TBLS.TBL_ID=TABLE_PARAMS.TBL_ID AND TBLS.DB_ID=DBS.DB_ID AND TABLE_PARAMS.PARAM_KEY='totalSize' AND  DBS.NAME=" + "'" + dbName + "' AND  TBLS.TBL_NAME=" + "'" + tableName + "'";
            } else {

                sql = "select SUM(PARTITION_PARAMS.PARAM_VALUE) as totalSize  from DBS,TBLS,PARTITIONS ,PARTITION_PARAMS where DBS.DB_ID=TBLS.DB_ID AND TBLS.TBL_ID=PARTITIONS.TBL_ID AND  PARTITIONS.PART_ID =PARTITION_PARAMS.PART_ID  AND PARTITION_PARAMS.PARAM_KEY='totalSize' AND DBS.NAME=" + "'" + dbName + "' AND TBLS.TBL_NAME=" + "'" + tableName + "' group by TBLS.TBL_NAME";
            }
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                res = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException(23001, e.getMessage());
        }
        return res;
    }

    @Override
    public List<PartInfo> getPartInfo(String dbName, String tableName) throws DAOException {
        DataSource dataSource = DataSourceUtil.getDataSource();

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PartInfo> PartInfos = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            String sql = "select b.PART_NAME,b.CREATE_TIME,MAX(CASE c.PARAM_KEY WHEN 'transient_lastDdlTime' THEN c.PARAM_VALUE ELSE null END) transient_lastDdlTime ,MAX(CASE c.PARAM_KEY WHEN 'numRows' THEN c.PARAM_VALUE ELSE null END) numRows,MAX(CASE c.PARAM_KEY WHEN 'totalSize' THEN c.PARAM_VALUE ELSE null END) totalSize, MAX(CASE c.PARAM_KEY WHEN 'numFiles' THEN c.PARAM_VALUE ELSE null END) numFiles  from TBLS a,PARTITIONS b,PARTITION_PARAMS c,DBS d where  a.TBL_NAME=" + "'" + tableName + "'" + "AND d.NAME=" + "'" + dbName + "'" + "AND a.TBL_ID=b.TBL_ID AND a.DB_ID=d.DB_ID AND b.PART_ID=c.PART_ID  GROUP BY c.PART_ID";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                PartInfo part = new PartInfo();
                part.setPartName(rs.getString(1));
                part.setCreateTime(DateUtil.unixToTimeStr(Long.valueOf(rs.getInt(2)) * 1000));
                part.setLastAccessTime(DateUtil.unixToTimeStr(Long.valueOf(rs.getInt(3)) * 1000));
                part.setReordCnt(rs.getInt(4));
                part.setStore(rs.getInt(5));
                part.setFileCount(rs.getInt(6));
                PartInfos.add(part);
            }

        } catch (SQLException e) {
            throw new DAOException(23001, e.getMessage());
        }

        return PartInfos;
    }
}
