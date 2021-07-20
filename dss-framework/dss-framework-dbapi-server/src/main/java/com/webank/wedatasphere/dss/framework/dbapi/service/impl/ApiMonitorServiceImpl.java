package com.webank.wedatasphere.dss.framework.dbapi.service.impl;

import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiAuthMapper;
import com.webank.wedatasphere.dss.framework.dbapi.service.ApiMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname ApiMonitorServiceImpl
 * @Description TODO
 * @Date 2021/7/20 12:27
 * @Created by suyc
 */
@Service
public class ApiMonitorServiceImpl implements ApiMonitorService {
    @Autowired
    private ApiAuthMapper apiAuthMapper;

    @Override
    public Long getOnlineApiCnt(Long workspaceId) {
        return apiAuthMapper.getOnlineApiCnt(workspaceId);
    }

    @Override
    public Long getOfflineApiCnt(Long workspaceId) {
        return apiAuthMapper.getOfflineApiCnt(workspaceId);
    }
}
