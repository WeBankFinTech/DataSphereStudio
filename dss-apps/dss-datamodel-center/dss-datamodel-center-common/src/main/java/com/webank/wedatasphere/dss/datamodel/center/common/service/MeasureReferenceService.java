package com.webank.wedatasphere.dss.datamodel.center.common.service;


public interface MeasureReferenceService {
    /**
     * 度量被引用情况
     * @param name
     * @return
     */
    int measureReferenceCount(String name);
}
