package com.webank.wedatasphere.dss.datamodel.table.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableColumns;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableVersion;

import java.util.List;


public interface TableVersionService extends IService<DssDatamodelTableVersion> {

    /**
     * 根据表名称查找最大版本
     * @param name
     * @return
     */
    String findLastVersion(String name);


    /**
     * 增加新版本
     * @param orgVersion
     * @param orgColumns
     * @return
     */
     Long addOlderVersion(DssDatamodelTable orgVersion, List<DssDatamodelTableColumns> orgColumns);



    /**
     * 查询指定版本
     * @param name
     * @param version
     * @return
     */
    DssDatamodelTableVersion findBackup(String name,String version);


    /**
     * 表内容引用情况
     * @param content
     * @return
     */
    int tableContentReference(String content);


    /**
     * 字段内容引用情况
     * @param content
     * @return
     */
    int tableColumnsReference(String content);
}
