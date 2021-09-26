package com.webank.wedatasphere.warehouse.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.webank.wedatasphere.warehouse.dao.domain.DwStatisticalPeriod;
import com.webank.wedatasphere.warehouse.dao.vo.DwStatisticalPeriodVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DwStatisticalPeriodMapper extends BaseMapper<DwStatisticalPeriod> {
    IPage<DwStatisticalPeriodVo> selectPageItems(IPage<DwStatisticalPeriod> queryPage, @Param(Constants.WRAPPER) QueryWrapper<DwStatisticalPeriod> queryWrapper);

    List<DwStatisticalPeriodVo> selectItems(@Param(Constants.WRAPPER) QueryWrapper<DwStatisticalPeriod> queryWrapper);

    DwStatisticalPeriodVo selectItemById(@Param(value = "id") Long id);
}
