package com.webank.wedatasphere.dss.datamodel.center.common.service;


public interface IndicatorTableCheckService {
    /**
     *
     * @param name
     * @return
     */
    Boolean referenceCase(String name);


    Boolean referenceEn(String name);
}
