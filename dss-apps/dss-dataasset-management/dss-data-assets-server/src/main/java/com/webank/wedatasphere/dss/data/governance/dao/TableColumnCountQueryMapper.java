package com.webank.wedatasphere.dss.data.governance.dao;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.webank.wedatasphere.dss.data.governance.entity.TableColumnCount;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TableColumnCountQueryMapper extends BaseMapper<TableColumnCount> {

    String querySql = "select tbl_db.db_name as db_name,tbl_db.tbl_name as tbl_name,tbl_db.tbl_id as tbl_id , count(*) as column_count \n" +
            "from COLUMNS_V2  as col\n" +
            "    left join CDS C on C.CD_ID = col.CD_ID\n" +
            "    right join (select t.TBL_NAME as tbl_name,t.TBL_ID as tbl_id, s.CD_ID as cd_id ,d.DB_ID as db_id ,d.NAME as db_name from  TBLS as t left join SDS as s on t.SD_ID = s.SD_ID left join DBS d on t.DB_ID = d.DB_ID) as tbl_db\n" +
            "    on tbl_db.cd_id = col.CD_ID\n" +
            "    ${ew.customSqlSegment} " +
            "    group by tbl_db.tbl_id ";

    String wrapperSql = "SELECT * from ( " + querySql + " ) AS q ${ew.customSqlSegment}";


    @Select(querySql)
    List<TableColumnCount> query(@Param(Constants.WRAPPER) Wrapper queryWrapper);
}
