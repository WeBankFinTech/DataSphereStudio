package com.webank.wedatasphere.dss.data.governance.dao;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.webank.wedatasphere.dss.data.governance.entity.TablePartitionSizeInfo;
import com.webank.wedatasphere.dss.data.governance.entity.TableSizeInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TableSizePartitionInfoMapper extends BaseMapper<TablePartitionSizeInfo> {

    String querySql = "select tbl_db.TBL_ID as id, tbl_db.tbl_name as tbl_name, tbl_db.db_name as db_name, p.PART_ID as part_id,p.LAST_ACCESS_TIME as last_access_time,p.SD_ID as sd_id,pp.PARAM_KEY as param_key,pp.PARAM_VALUE as param_value from PARTITIONS AS p left join PARTITION_PARAMS pp on pp.PART_ID = p.PART_ID  left join\n" +
            "(select tbls.TBL_ID as tbl_id, tbls.TBL_NAME as tbl_name, db.NAME as db_name from TBLS as tbls, DBS as db where tbls.DB_ID = db.DB_ID ) as tbl_db on tbl_db.tbl_id = p.TBL_ID\n";

    String wrapperSql = "SELECT * from ( " + querySql + " ) AS q ${ew.customSqlSegment}";


    @Select(wrapperSql)
    List<TablePartitionSizeInfo> query(@Param(Constants.WRAPPER) Wrapper queryWrapper);
}
