package com.webank.wedatasphere.dss.data.asset.dao;

import com.webank.wedatasphere.dss.data.asset.entity.HivePartInfo;
import com.webank.wedatasphere.dss.data.asset.entity.HiveStorageInfo;
import org.apache.ibatis.annotations.*;

import java.sql.SQLException;
import java.util.List;

@Mapper
public interface MetaInfoMapper {
    Long getTableStorage() throws SQLException;
    List<HiveStorageInfo> getTop10Table() throws SQLException;
    int getTableInfo(@Param("dbName") String dbName,@Param("tableName") String tableName,@Param("isPartTable") Boolean isPartTable) throws  SQLException;

    List<HivePartInfo> getPartInfo(@Param("dbName") String dbName, @Param("tableName") String tableName) throws SQLException;



}
