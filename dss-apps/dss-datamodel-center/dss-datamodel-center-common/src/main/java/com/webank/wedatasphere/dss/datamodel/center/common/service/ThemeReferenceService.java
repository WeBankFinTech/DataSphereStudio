package com.webank.wedatasphere.dss.datamodel.center.common.service;


public interface ThemeReferenceService {
    /**
     * 主题被引用情况
     * @param name
     * @return
     */
    int themeReferenceCount(String name);
}
