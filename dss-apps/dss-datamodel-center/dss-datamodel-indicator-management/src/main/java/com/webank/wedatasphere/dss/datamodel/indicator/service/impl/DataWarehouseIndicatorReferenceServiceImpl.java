package com.webank.wedatasphere.dss.datamodel.indicator.service.impl;

import com.webank.wedatasphere.dss.datamodel.center.common.service.DataWarehouseIndicatorReferenceService;
import com.webank.wedatasphere.dss.datamodel.indicator.service.IndicatorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DataWarehouseIndicatorReferenceServiceImpl implements DataWarehouseIndicatorReferenceService {

    @Resource
    private IndicatorService indicatorService;


    @Override
    public int indicatorCycleReferenceCount(String name) {
        return indicatorService.indicatorCycleReferenceCount(name);
    }

    @Override
    public int indicatorLayerReferenceCount(String name) {
        return indicatorService.indicatorLayerReferenceCount(name);
    }

    @Override
    public int indicatorModifierReferenceCount(String name) {
        return indicatorService.indicatorModifierReferenceCount(name);
    }

    @Override
    public int indicatorThemeReferenceCount(String name) {
        return indicatorService.indicatorThemeReferenceCount(name);
    }
}
