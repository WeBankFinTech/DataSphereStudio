package com.webank.wedatasphere.dss.framework.dbapi.service.impl;

import com.webank.wedatasphere.dss.framework.dbapi.dao.DSSDataApiAuthMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.DSSDataApiAuth;
import com.webank.wedatasphere.dss.framework.dbapi.service.DSSDataApiAuthService;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname DSSDataApiAuthServiceImpl
 * @Description TODO
 * @Date 2021/7/13 17:13
 * @Created by suyc
 */
@Service
public class DSSDataApiAuthServiceImpl implements DSSDataApiAuthService {
    @Autowired
    private DSSDataApiAuthMapper dssDataApiAuthMapper;

    @Override
    public Long createDataApiAuth(DSSDataApiAuth dssDataApiAuth) throws ErrorException {
        dssDataApiAuthMapper.insert(dssDataApiAuth);
        return dssDataApiAuth.getId();
    }
}
