package com.webank.wedatasphere.dss.data.governance.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.webank.wedatasphere.dss.data.governance.entity.TableInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface TableStorageInfoMapper extends BaseMapper<TableInfo> {

        //普通表获取表名和存储量
        String sql= "select CONCAT(DBS.NAME, '.', TBLS.TBL_NAME) AS table_name, CASE WHEN TABLE_PARAMS.PARAM_KEY = 'totalSize' THEN TABLE_PARAMS.PARAM_VALUE ELSE 0 END as totalSize\n" +
                "from DBS, TBLS,TABLE_PARAMS\n" +
                "where TBLS.TBL_ID=TABLE_PARAMS.TBL_ID AND TBLS.DB_ID=DBS.DB_ID AND TABLE_PARAMS.PARAM_KEY = 'totalSize'";

        //分区表获取表名和存储量
        String sql2="select CONCAT(DBS.NAME, '.', TBLS.TBL_NAME) AS table_name,SUM(CASE WHEN PARTITION_PARAMS.PARAM_KEY = 'totalSize' THEN PARTITION_PARAMS.PARAM_VALUE ELSE 0 END) as totalSize\n" +
                "  from DBS,TBLS,PARTITIONS ,PARTITION_PARAMS \n" +
                "  where DBS.DB_ID=TBLS.DB_ID AND TBLS.TBL_ID=PARTITIONS.TBL_ID AND  PARTITIONS.PART_ID =PARTITION_PARAMS.PART_ID AND PARTITION_PARAMS.PARAM_KEY = 'totalSize'\n" +
                "  group by table_name";

        //合并分区表和普通表查询结果，并根据"DB.Table"过滤
        String queryWrapperSql = "select table_name as tableName,totalSize as storage FROM (( " + sql  + ")" + " UNION " +"(" + sql2 +")) as q ${ew.customSqlSegment}";

        //合并分区表和普通表查询结果
        String querySql = "select table_name as tableName,totalSize as storage FROM (( " + sql  + ")" + " UNION " +"(" + sql2 +")) as q";

        @Select(querySql)
        List<TableInfo> query();

        @Select(queryWrapperSql)
        List<TableInfo> queryByTableName(@Param(Constants.WRAPPER) Wrapper queryWrapper);

}
