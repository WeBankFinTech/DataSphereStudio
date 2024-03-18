package com.webank.wedatasphere.dss.data.governance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface TableStorageMapper extends BaseMapper<Long> {

    String sql="select SUM(PARAM_VALUE) as storage from TABLE_PARAMS WHERE PARAM_KEY='totalSize'";

    String sql2="select SUM(PARAM_VALUE) as storage from PARTITION_PARAMS WHERE PARAM_KEY='totalSize'";

    String querySql = "select sum(storage) as storage from((" + sql + ") union all (" + sql2 + ")) as q";

    @Select(querySql)
    List<Long> getTableStorage();

    String commonSql="select TABLE_PARAMS.PARAM_VALUE as totalSize from DBS, TBLS,TABLE_PARAMS where TBLS.TBL_ID=TABLE_PARAMS.TBL_ID AND TBLS.DB_ID=DBS.DB_ID AND TABLE_PARAMS.PARAM_KEY='totalSize' AND  DBS.NAME= #{dbName} AND  TBLS.TBL_NAME=#{tableName}";
    @Select(commonSql)
    List<Long> getTableInfo(@Param("dbName") String dbName,@Param("tableName") String tableName);


    String partitionSql="select SUM(PARTITION_PARAMS.PARAM_VALUE) as totalSize\n" +
            "from DBS,TBLS,PARTITIONS ,PARTITION_PARAMS\n" +
            "where DBS.DB_ID=TBLS.DB_ID AND TBLS.TBL_ID=PARTITIONS.TBL_ID AND  PARTITIONS.PART_ID =PARTITION_PARAMS.PART_ID  AND PARTITION_PARAMS.PARAM_KEY='totalSize' AND DBS.NAME= #{dbName} AND TBLS.TBL_NAME= #{tableName}\n" +
            "group by TBLS.TBL_NAME";

    @Select(partitionSql)
    List<Long> getPartTableInfo(@Param("dbName") String dbName,@Param("tableName") String tableName);
}
