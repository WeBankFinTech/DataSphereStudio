package com.webank.wedatasphere.dss.datamodel.table.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableStats;

public interface TableStatsService extends IService<DssDatamodelTableStats> {

    /**
     * 搜索指定版本表指定版本统计信息
     * @param tableName
     * @return
     */
    DssDatamodelTableStats queryByTableName(String tableName);
}
