package com.webank.wedatasphere.dss.datamodel.table.service.impl;

import com.webank.wedatasphere.dss.datamodel.center.common.service.DataWarehouseLabelReferenceService;
import com.webank.wedatasphere.dss.datamodel.table.service.LabelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DataWarehouseLabelReferenceServiceImpl implements DataWarehouseLabelReferenceService {

    @Resource
    private LabelService labelService;

    @Override
    public int labelThemeReferenceCount(String name) {
        return labelService.labelThemeReferenceCount(name);
    }
}
