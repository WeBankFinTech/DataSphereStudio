package com.webank.wedatasphere.dss.datamodel.center.common.service;



public interface LayerReferenceService {
    /**
     * 分层被引用情况
     * @param name
     * @return
     */
    int layerReferenceCount(String name);
}
