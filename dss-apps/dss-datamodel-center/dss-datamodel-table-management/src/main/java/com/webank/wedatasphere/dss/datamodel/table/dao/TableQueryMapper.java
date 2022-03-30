package com.webank.wedatasphere.dss.datamodel.table.dao;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTabelQuery;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TableQueryMapper extends BaseMapper<DssDatamodelTabelQuery> {
    String querySql = "SELECT a.*, any_value(b.model_type) as model_type,any_value(b.model_name) as model_name FROM dss_datamodel_table AS a LEFT JOIN dss_datamodel_table_columns AS b ON b.table_id = a.id " +
                " ${ew.customSqlSegment}";
    String wrapperSql = "SELECT * from ( " + querySql + " ) AS q ${ew.customSqlSegment}";



    @Select(querySql)
    List<DssDatamodelTabelQuery> page(@Param(Constants.WRAPPER) Wrapper queryWrapper);
}
