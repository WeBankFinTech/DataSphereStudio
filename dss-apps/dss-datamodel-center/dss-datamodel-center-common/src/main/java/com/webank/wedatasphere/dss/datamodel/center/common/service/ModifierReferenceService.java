package com.webank.wedatasphere.dss.datamodel.center.common.service;


public interface ModifierReferenceService {
    /**
     * 修饰词被引用情况
     * @param name
     * @return
     */
    int modifierReferenceCount(String name);
}
