package com.webank.wedatasphere.dss.datamodel.indicator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.indicator.dto.IndicatorVersionDTO;
import com.webank.wedatasphere.dss.datamodel.indicator.entity.DssDatamodelIndicatorVersion;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;


public interface IndicatorVersionService extends IService<DssDatamodelIndicatorVersion> {


    /**
     * 保存旧版本
     *
     * @param name
     * @param owner
     * @param principalName
     * @param version
     * @param versionContext
     * @return
     */
    int addOlderVersion(String name, String owner, String principalName, String version, String comment, IndicatorVersionDTO versionContext) throws ErrorException;

    /**
     * 根据指标名称查找最大版本
     * @param name
     * @return
     */
    String findLastVersion(String name);


    /**
     * 查询指定版本指标
     * @param name
     * @param version
     * @return
     */
    DssDatamodelIndicatorVersion findBackup(String name,String version);


    /**
     * 引用情况
     * @param context
     * @return
     */
    int contentReferenceCount(String context);
}
