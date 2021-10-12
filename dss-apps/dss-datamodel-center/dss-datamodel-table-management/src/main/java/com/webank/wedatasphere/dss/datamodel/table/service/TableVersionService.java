package com.webank.wedatasphere.dss.datamodel.table.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;


public interface TableVersionService extends IService<DssDatamodelTable> {

    /**
     * 根据表名称查找最大版本
     * @param name
     * @return
     */
    String findLastVersion(String name);
}
