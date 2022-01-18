package com.webank.wedatasphere.dss.datamodel.measure.service.impl;


import com.webank.wedatasphere.dss.datamodel.center.common.service.DataWarehouseMeasuredReferenceService;
import com.webank.wedatasphere.dss.datamodel.measure.service.MeasureService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DataWarehouseMeasuredReferenceServiceImpl implements DataWarehouseMeasuredReferenceService {

    @Resource
    private MeasureService measureService;

    @Override
    public int measureThemeReferenceCount(String name) {
        return measureService.measureThemeReferenceCount(name);
    }
}
