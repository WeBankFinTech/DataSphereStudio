package com.webank.wedatasphere.warehouse.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.warehouse.dao.domain.DwModifier;
import com.webank.wedatasphere.warehouse.dao.domain.DwStatisticalPeriod;
import com.webank.wedatasphere.warehouse.dao.mapper.DwModifierMapper;
import com.webank.wedatasphere.warehouse.dao.mapper.DwStatisticalPeriodMapper;
import com.webank.wedatasphere.warehouse.exception.DwException;
import com.webank.wedatasphere.warehouse.utils.PreconditionUtil;

import java.util.Optional;

public interface DwDomainInUseCheckAdapter {

    DwStatisticalPeriodMapper getDwStatisticalPeriodMapper();

    DwModifierMapper getDwModifierMapper();

    default void checkMapper() throws DwException {
        DwModifierMapper dwModifierMapper = getDwModifierMapper();
        PreconditionUtil.checkState(Optional.ofNullable(dwModifierMapper).isPresent(), DwException.stateReject("dw modifier mapper should not be null"));

        DwStatisticalPeriodMapper dwStatisticalPeriodMapper = getDwStatisticalPeriodMapper();
        PreconditionUtil.checkState(Optional.ofNullable(dwStatisticalPeriodMapper).isPresent(), DwException.stateReject("dw statistical period mapper should not be null"));
    }

    default boolean isThemeDomainInUse(Long id) throws DwException {
        checkMapper();

        // 检查修饰词关联数量
        QueryWrapper<DwModifier> queryModifierWrapper = new QueryWrapper<>();
        queryModifierWrapper.eq("theme_domain_id", id);
        queryModifierWrapper.eq("status", Boolean.TRUE);
        queryModifierWrapper.eq("is_available", Boolean.TRUE);
        Integer count1 = getDwModifierMapper().selectCount(queryModifierWrapper);
        boolean isRelModifier = Optional.ofNullable(count1).orElse(0) > 0;

        // 检查统计周期关联数量
        QueryWrapper<DwStatisticalPeriod> statisticalPeriodQueryWrapper = new QueryWrapper<>();
        statisticalPeriodQueryWrapper.eq("theme_domain_id", id);
        statisticalPeriodQueryWrapper.eq("status", Boolean.TRUE);
        statisticalPeriodQueryWrapper.eq("is_available", Boolean.TRUE);
        Integer count2 = getDwStatisticalPeriodMapper().selectCount(statisticalPeriodQueryWrapper);
        boolean isRelStatisticalPeriod = Optional.ofNullable(count2).orElse(0) > 0;

        // check datamodel
        // TODO 绑定关联关系校验
//        boolean isRelXxxx = dataModelClient.xxxx();

        return isRelModifier || isRelStatisticalPeriod;
    }

    default boolean isLayerInUse(Long id) throws DwException {
        checkMapper();

        // 检查修饰词关联数量
        QueryWrapper<DwModifier> queryModifierWrapper = new QueryWrapper<>();
        queryModifierWrapper.eq("layer_id", id);
        queryModifierWrapper.eq("status", Boolean.TRUE);
        queryModifierWrapper.eq("is_available", Boolean.TRUE);
        Integer count1 = getDwModifierMapper().selectCount(queryModifierWrapper);
        boolean isRelModifier = Optional.ofNullable(count1).orElse(0) > 0;

        // 检查统计周期关联数量
        QueryWrapper<DwStatisticalPeriod> statisticalPeriodQueryWrapper = new QueryWrapper<>();
        statisticalPeriodQueryWrapper.eq("layer_id", id);
        statisticalPeriodQueryWrapper.eq("status", Boolean.TRUE);
        statisticalPeriodQueryWrapper.eq("is_available", Boolean.TRUE);
        Integer count2 = getDwStatisticalPeriodMapper().selectCount(statisticalPeriodQueryWrapper);
        boolean isRelStatisticalPeriod = Optional.ofNullable(count2).orElse(0) > 0;

        return isRelModifier || isRelStatisticalPeriod;
    }

}
