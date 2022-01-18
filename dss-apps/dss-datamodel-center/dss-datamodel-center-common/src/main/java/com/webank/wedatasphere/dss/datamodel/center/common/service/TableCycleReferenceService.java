package com.webank.wedatasphere.dss.datamodel.center.common.service;


public interface TableCycleReferenceService {
    /**
     * 周期引用情况
     * @param name
     * @return
     */
    int tableCycleReferenceCount(String name);
}
