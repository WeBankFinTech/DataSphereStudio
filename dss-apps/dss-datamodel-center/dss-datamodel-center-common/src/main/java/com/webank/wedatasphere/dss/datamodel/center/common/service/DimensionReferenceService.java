package com.webank.wedatasphere.dss.datamodel.center.common.service;


public interface DimensionReferenceService {

    /**
     * 维度被引用情况
     * @param name
     * @return
     */
    int dimensionReferenceCount(String name);
}
