package com.webank.wedatasphere.dss.datamodel.center.common.service;


public interface DimensionTableCheckService {
    /**
     *
     * @param name
     * @return
     */
    Boolean referenceCase(String name);


    /**
     *
     * @param name
     * @return
     */
    Boolean referenceCaseEn(String name);
}
