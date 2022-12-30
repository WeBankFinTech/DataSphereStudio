package com.webank.wedatasphere.dss.datamodel.dimension.service.impl;


import com.webank.wedatasphere.dss.datamodel.center.common.service.DataWarehouseDimensionReferenceService;
import com.webank.wedatasphere.dss.datamodel.dimension.service.DimensionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DataWarehouseDimensionReferenceServiceImpl implements DataWarehouseDimensionReferenceService {

    @Resource
    private DimensionService dimensionService;

    @Override
    public int dimensionThemeReferenceCount(String name) {
        return dimensionService.dimensionThemeReferenceCount(name);
    }
}
