package com.webank.wedatasphere.dss.datamodel.table.service.impl;

import com.webank.wedatasphere.dss.datamodel.center.common.service.DataWarehouseTableReferenceService;
import com.webank.wedatasphere.dss.datamodel.table.service.TableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DataWarehouseTableReferenceServiceImpl implements DataWarehouseTableReferenceService {

    @Resource
    private TableService tableService;

    @Override
    public int tableLayerReferenceCount(String name) {
        return tableService.tableLayerReferenceCount(name);
    }

    @Override
    public int tableModifierReferenceCount(String name) {
        return tableService.tableModifierReferenceCount(name);
    }

    @Override
    public int tableThemeReferenceCount(String name) {
        return tableService.tableThemeReferenceCount(name);
    }

    @Override
    public int tableCycleReferenceCount(String name) {
        return tableService.tableCycleReferenceCount(name);
    }
}
