package com.webank.wedatasphere.dss.datamodel.table.service.impl;

import com.webank.wedatasphere.dss.datamodel.center.common.service.TableThemeReferenceService;
import com.webank.wedatasphere.dss.datamodel.table.service.TableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TableThemeReferenceServiceImpl implements TableThemeReferenceService {

    @Resource
    private TableService tableService;

    @Override
    public int tableThemeReferenceCount(String name) {
        return tableService.tableThemeReferenceCount(name);
    }
}
