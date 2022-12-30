package com.webank.wedatasphere.dss.datamodel.center.common.service;


public interface IndicatorReferenceService {

    /**
     * 指标被引用情况
     * @param name
     * @return
     */
    int indicatorReferenceCount(String name);
}
