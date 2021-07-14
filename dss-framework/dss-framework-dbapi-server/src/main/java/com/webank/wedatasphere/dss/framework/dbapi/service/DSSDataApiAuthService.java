package com.webank.wedatasphere.dss.framework.dbapi.service;

import com.webank.wedatasphere.dss.framework.dbapi.entity.DSSDataApiAuth;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

/**
 * @Classname DSSDataApiAuthService
 * @Description TODO
 * @Date 2021/7/13 17:10
 * @Created by suyc
 */
public interface DSSDataApiAuthService {
    public Long createDataApiAuth(DSSDataApiAuth dssDataApiAuth) throws ErrorException;
}
