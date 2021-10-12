package com.webank.wedatasphere.dss.datamodel.table.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableColumns;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

import java.util.List;

public interface TableColumnsService extends IService<DssDatamodelTableColumns> {

    /**
     * 批量新增表字段
     * @param columnsList
     * @return
     */
    int batchInsert(Long tableId,List<DssDatamodelTableColumns> columnsList) throws ErrorException;


    /**
     * 批量更新表字段
     * @param tableId
     * @param columnsList
     * @return
     * @throws ErrorException
     */
    int batchUpdate(Long tableId,List<DssDatamodelTableColumns> columnsList) throws ErrorException;


    /**
     * 表字段列表
     * @param tableId
     * @return
     * @throws ErrorException
     */
    List<DssDatamodelTableColumns> listByTableId(Long tableId);
}
