package com.webank.wedatasphere.dss.datamodel.indicator.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorQuery;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface IndicatorQueryMapper extends BaseMapper<DssDatamodelIndicatorQuery> {

    String querySql = "SELECT a.*, b.indicator_type, b.measure_id, b.indicator_source_info, b.formula, b.business, b.business_owner, b.calculation, b.calculation_owner " +
            " FROM dss_datamodel_indicator AS a LEFT JOIN dss_datamodel_indicator_content AS b ON b.indicator_id = a.id ${ew.customSqlSegment}";
    String wrapperSql = "SELECT * from ( " + querySql + " ) AS q ${ew.customSqlSegment}";

    @Select(querySql)
    List<DssDatamodelIndicatorQuery> page(@Param(Constants.WRAPPER) Wrapper queryWrapper);
}
