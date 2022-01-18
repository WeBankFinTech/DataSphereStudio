package com.webank.wedatasphere.dss.data.governance.dao;

import com.webank.wedatasphere.dss.data.governance.entity.PartInfo;
import com.webank.wedatasphere.dss.data.governance.entity.TableInfo;
import com.webank.wedatasphere.dss.data.governance.exception.DAOException;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;


@Mapper
public interface MetaInfoMapper {
    Long getTableStorage() throws DAOException;
    List<TableInfo> getTop10Table() throws DAOException;
    int getTableInfo(@Param("dbName") String dbName,@Param("tableName") String tableName,@Param("isPartTable") Boolean isPartTable) throws  DAOException;
    List<PartInfo> getPartInfo(@Param("dbName") String dbName, @Param("tableName") String tableName) throws DAOException;
}
