package com.webank.wedatasphere.dss.datamodel.indicator.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.webank.wedatasphere.dss.datamodel.center.common.service.DatamodelIndicatorReferenceService;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorContent;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorContentService;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorService;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorVersionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DatamodelIndicatorReferenceServiceImpl implements DatamodelIndicatorReferenceService {

    @Resource
    public IndicatorService indicatorService;

    @Resource
    public IndicatorContentService indicatorContentService;

    @Resource
    public IndicatorVersionService indicatorVersionService;

    @Override
    public int indicatorDimensionReferenceCount(String name) {
        return indicatorService.indicatorDimensionReferenceCount(name);
    }

    @Override
    public int indicatorMeasuredReference(String name) {
        return indicatorService.indicatorMeasureReferenceCount(name);
    }

    @Override
    public int indicatorIndicatorCount(String name) {
        return indicatorService.indicatorIndicatorReferenceCount(name);
    }
}
