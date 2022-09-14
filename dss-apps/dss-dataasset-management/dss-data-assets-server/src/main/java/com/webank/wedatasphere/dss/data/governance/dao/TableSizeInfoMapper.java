package com.webank.wedatasphere.dss.data.governance.dao;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.webank.wedatasphere.dss.data.governance.entity.TableSizeInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TableSizeInfoMapper extends BaseMapper<TableSizeInfo> {
    String querySql = "select tbls.TBL_ID as id,d.NAME as db_name,tbls.TBL_NAME as tbl_name, tp.PARAM_KEY as param_key, tp.PARAM_VALUE as param_value\n" +
            "from TBLS as tbls left join TABLE_PARAMS as tp on tbls.TBL_ID = tp.TBL_ID  left join DBS d on tbls.DB_ID = d.DB_ID \n";;

    String wrapperSql = "SELECT * from ( " + querySql + " ) AS q ${ew.customSqlSegment}";


    @Select(wrapperSql)
    List<TableSizeInfo> query(@Param(Constants.WRAPPER) Wrapper queryWrapper);
}
