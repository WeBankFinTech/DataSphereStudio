package com.webank.wedatasphere.dss.data.governance.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.dss.data.governance.entity.PartInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface PartInfoMapper extends BaseMapper<PartInfo> {

        String sql = "select b.PART_NAME,b.CREATE_TIME,\n" +
                " MAX(CASE c.PARAM_KEY WHEN 'transient_lastDdlTime' THEN c.PARAM_VALUE ELSE null END) last_access_time ,\n" +
                " MAX(CASE c.PARAM_KEY WHEN 'numRows' THEN c.PARAM_VALUE ELSE null END) reord_cnt,\n" +
                " MAX(CASE c.PARAM_KEY WHEN 'totalSize' THEN c.PARAM_VALUE ELSE null END) store,\n" +
                " MAX(CASE c.PARAM_KEY WHEN 'numFiles' THEN c.PARAM_VALUE ELSE null END) file_count\n" +
                " from TBLS a,PARTITIONS b,PARTITION_PARAMS c,DBS d \n" +
                " where  a.TBL_NAME= #{tableName} AND d.NAME= #{dbName} AND a.TBL_ID=b.TBL_ID AND a.DB_ID=d.DB_ID AND b.PART_ID=c.PART_ID \n" +
                " GROUP BY c.PART_ID";
        @Select(sql)
        List<PartInfo> query(@Param("dbName") String dbName, @Param("tableName") String tableName) ;

}
