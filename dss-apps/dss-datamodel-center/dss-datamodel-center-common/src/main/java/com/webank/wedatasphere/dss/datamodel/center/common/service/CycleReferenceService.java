package com.webank.wedatasphere.dss.datamodel.center.common.service;


public interface CycleReferenceService {
    /**
     * 周期被引用情况
     * @param name
     * @return
     */
    int cycleReferenceCount(String name);
}
