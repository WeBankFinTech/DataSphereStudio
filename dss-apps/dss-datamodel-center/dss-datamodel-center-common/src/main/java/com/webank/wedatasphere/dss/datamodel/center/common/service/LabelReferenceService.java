package com.webank.wedatasphere.dss.datamodel.center.common.service;


public interface LabelReferenceService {
    /**
     * 标签被引用情况
     * @param name
     * @return
     */
    int labelReferenceCount(String name);
}
