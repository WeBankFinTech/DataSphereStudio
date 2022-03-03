package com.webank.wedatasphere.dss.datamodel.table.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.center.common.dto.CreateTableDTO;
import com.webank.wedatasphere.dss.datamodel.table.dto.HiveTblSimpleInfoDTO;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableMaterializedHistory;
import org.apache.linkis.common.exception.ErrorException;

import java.util.Optional;

public interface TableMaterializedHistoryService extends IService<DssDatamodelTableMaterializedHistory> {

    /**
     * 物化表结构
     * @param current
     * @param user
     * @return
     */
    Integer materializedTable(DssDatamodelTable current, String user) throws ErrorException;


    /**
     * 生成建表语句
     * @param current
     * @return
     */
    String generateSql(DssDatamodelTable current);


    /**
     * 检查是否有数据
     * @param current
     * @param user
     * @throws ErrorException
     */
    void checkData(DssDatamodelTable current, String user) throws ErrorException;


    /**
     * 判断是否有数据
     *
     * @param tableName @return
     * @param user
     */
    boolean hasData(String tableName, String user);


    /**
     *
     * @param tableName
     * @param user
     * @throws ErrorException
     * @return
     */
    boolean tableExists(String tableName, String user) throws ErrorException;


    /**
     * 删除表
     * @param tableName
     * @param user
     * @return
     * @throws ErrorException
     */
    boolean dropTable(String tableName, String user) throws ErrorException;


    /**
     * 创建表
     * @param current
     * @param user
     * @return
     * @throws ErrorException
     */
    CreateTableDTO createTable(DssDatamodelTable current, String user) throws ErrorException;


    /**
     * 查看是否物化
     * @param tableName
     * @param version
     * @return
     * @throws ErrorException
     */
    boolean isMaterialized(String tableName, String version) throws ErrorException;


    /**
     * 根据表名查询查询资产
     * @param tableName
     * @return
     * @throws ErrorException
     */
    Optional<HiveTblSimpleInfoDTO> getHiveTblSimpleInfoByName(String tableName, String user)throws ErrorException;
}
