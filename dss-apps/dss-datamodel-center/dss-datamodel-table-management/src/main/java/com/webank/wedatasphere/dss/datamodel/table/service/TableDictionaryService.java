package com.webank.wedatasphere.dss.datamodel.table.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelDictionary;

import java.util.List;

public interface TableDictionaryService extends IService<DssDatamodelDictionary> {

    /**
     * 根据类型搜索字典
     * @param type
     * @return
     */
    List<DssDatamodelDictionary> listByType(String type);
}
