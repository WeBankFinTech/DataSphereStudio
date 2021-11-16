package com.webank.wedatasphere.dss.datamodel.center.common.service.impl;

import com.webank.wedatasphere.dss.datamodel.center.common.service.DatamodelIndicatorReferenceService;
import com.webank.wedatasphere.dss.datamodel.center.common.service.DatamodelReferencService;
import com.webank.wedatasphere.dss.datamodel.center.common.service.DatamodelTableReferenceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DatamodelReferenceServiceImpl implements DatamodelReferencService {

    @Resource
    private DatamodelIndicatorReferenceService datamodelIndicatorReferenceService;

    @Resource
    private DatamodelTableReferenceService datamodelTableReferenceService;

    @Override
    public int dimensionReferenceCount(String name) {
        return datamodelIndicatorReferenceService.indicatorDimensionReferenceCount(name)
                + datamodelTableReferenceService.tableDimensionReferenceCount(name);
    }

    @Override
    public int indicatorReferenceCount(String name) {
        return datamodelTableReferenceService.tableIndicatorReferenceCount(name);
    }

    @Override
    public int measureReferenceCount(String name) {
        return datamodelIndicatorReferenceService.indicatorMeasuredReference(name)
                + datamodelTableReferenceService.tableMeasuredReferenceService(name);
    }
}
