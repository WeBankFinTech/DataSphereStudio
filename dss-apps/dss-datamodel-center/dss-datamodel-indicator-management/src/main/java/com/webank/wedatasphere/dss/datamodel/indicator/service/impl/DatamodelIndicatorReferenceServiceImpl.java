package com.webank.wedatasphere.dss.datamodel.indicator.service.impl;


import com.webank.wedatasphere.dss.datamodel.center.common.service.DatamodelIndicatorReferenceService;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DatamodelIndicatorReferenceServiceImpl implements DatamodelIndicatorReferenceService {

    @Resource
    public IndicatorService indicatorService;

    @Override
    public int indicatorDimensionReferenceCount(String name) {
        return indicatorService.indicatorDimensionReferenceCount(name);
    }

    @Override
    public int indicatorMeasuredReference(String name) {
        return indicatorService.indicatorMeasureReferenceCount(name);
    }
}
