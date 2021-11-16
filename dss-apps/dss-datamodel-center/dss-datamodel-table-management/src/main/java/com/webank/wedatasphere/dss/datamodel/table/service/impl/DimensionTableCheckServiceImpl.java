package com.webank.wedatasphere.dss.datamodel.table.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ModeType;
import com.webank.wedatasphere.dss.datamodel.center.common.service.DimensionTableCheckService;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableColumns;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableVersion;
import com.webank.wedatasphere.dss.datamodel.table.service.TableColumnsService;
import com.webank.wedatasphere.dss.datamodel.table.service.TableVersionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DimensionTableCheckServiceImpl implements DimensionTableCheckService {

    @Resource
    private TableColumnsService tableColumnsService;

    @Resource
    private TableVersionService tableVersionService;


    @Override
    public Boolean referenceCase(String name) {
        int columnCount = tableColumnsService.modelReferenceCount(ModeType.DIMENSION,name);
        int columnVersionCount = tableVersionService.tableContentReference(name);
        return columnCount > 0 || columnVersionCount>0 ;
    }


    @Override
    public Boolean referenceCaseEn(String name) {
        int columnCount = tableColumnsService.modelReferenceCountEn(ModeType.DIMENSION,name);
        int columnVersionCount = tableVersionService.tableContentReference(name);
        return columnCount > 0 || columnVersionCount>0 ;
    }

}
