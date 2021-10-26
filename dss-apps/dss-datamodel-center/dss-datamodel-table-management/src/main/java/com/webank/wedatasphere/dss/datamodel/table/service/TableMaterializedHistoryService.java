package com.webank.wedatasphere.dss.datamodel.table.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.center.common.dto.CreateTableDTO;
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


    /**
     * 检查是否有数据
     * @param current
     * @throws ErrorException
     */
    void checkData(DssDatamodelTable current) throws ErrorException;


    /**
     * 判断是否有数据
     *
     * @param tableName@return
     */
    boolean hasData(String tableName);


    /**
     *
     * @param tableName
     * @throws ErrorException
     * @return
     */
    boolean tableExists(String tableName) throws ErrorException;


    /**
     * 删除表
     * @param tableName
     * @return
     * @throws ErrorException
     */
    boolean dropTable(String tableName) throws ErrorException;


    /**
     * 创建表
     * @param current
     * @return
     * @throws ErrorException
     */
    CreateTableDTO createTable(DssDatamodelTable current) throws ErrorException;


    /**
     * 查看是否物化
     * @param tableName
     * @param version
     * @return
     * @throws ErrorException
     */
    boolean isMaterialized(String tableName, String version);
}
