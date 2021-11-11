package com.webank.wedatasphere.dss.datamodel.table.service.impl;


import com.webank.wedatasphere.dss.datamodel.center.common.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DataWarehouseReferenceServiceImpl implements DataWarehouseReferenceService {

    @Resource
    private DataWarehouseTableReferenceService tableReferenceService;

    @Resource
    private DataWarehouseIndicatorReferenceService indicatorReferenceService;

    @Resource
    private DataWarehouseMeasuredReferenceService measuredReferenceService;

    @Resource
    private DataWarehouseDimensionReferenceService dimensionReferenceService;

    @Override
    public int cycleReferenceCount(String name) {
        return tableReferenceService.tableCycleReferenceCount(name) + indicatorReferenceService.indicatorCycleReferenceCount(name);
    }

    @Override
    public int layerReferenceCount(String name) {
        return tableReferenceService.tableLayerReferenceCount(name) + indicatorReferenceService.indicatorLayerReferenceCount(name);
    }

    @Override
    public int modifierReferenceCount(String name) {
        return tableReferenceService.tableModifierReferenceCount(name) + indicatorReferenceService.indicatorModifierReferenceCount(name);
    }

    @Override
    public int themeReferenceCount(String name) {
        return tableReferenceService.tableThemeReferenceCount(name)
                + indicatorReferenceService.indicatorThemeReferenceCount(name)
                + dimensionReferenceService.dimensionThemeReferenceCount(name)
                + measuredReferenceService.dimensionThemeReferenceCount(name);
    }
}
