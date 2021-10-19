package com.webank.wedatasphere.dss.datamodel.table.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableMaterializedHistory;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

public interface TableMaterializedHistoryService extends IService<DssDatamodelTableMaterializedHistory> {

    /**
     * 物化表结构
     * @param current
     * @return
     */
    Integer materializedTable(DssDatamodelTable current) throws ErrorException;


    /**
     * 生成建表语句
     * @param current
     * @return
     */
    String generateSql(DssDatamodelTable current);
}
