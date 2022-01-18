package com.webank.wedatasphere.dss.datamodel.table.service.impl;


import com.webank.wedatasphere.dss.datamodel.center.common.service.DatamodelTableReferenceService;
import com.webank.wedatasphere.dss.datamodel.table.service.TableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DatamodelTableReferenceServiceImpl implements DatamodelTableReferenceService {

    @Resource
    private TableService tableService;

    @Override
    public int tableDimensionReferenceCount(String name) {
        return tableService.tableDimensionReferenceCount(name);
    }

    @Override
    public int tableIndicatorReferenceCount(String name) {
        return tableService.tableIndicatorReferenceCount(name);
    }

    @Override
    public int tableMeasuredReferenceService(String name) {
        return tableService.tableMeasureReferenceCount(name);
    }

    @Override
    public int tableLabelReferenceCount(String name) {
        return tableService.tableLabelReferenceCount(name);
    }
}
