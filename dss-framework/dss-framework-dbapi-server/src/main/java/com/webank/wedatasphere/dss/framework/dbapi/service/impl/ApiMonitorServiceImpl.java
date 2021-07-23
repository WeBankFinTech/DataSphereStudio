package com.webank.wedatasphere.dss.framework.dbapi.service.impl;

import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiCallMapper;
import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiConfigMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.request.CallMonitorResquest;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiCallInfoByCnt;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiCallInfoByFailRate;
import com.webank.wedatasphere.dss.framework.dbapi.service.ApiMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname ApiMonitorServiceImpl
 * @Description TODO
 * @Date 2021/7/20 12:27
 * @Created by suyc
 */
@Service
public class ApiMonitorServiceImpl implements ApiMonitorService {
    @Autowired
    private ApiConfigMapper apiConfigMapper;

    @Autowired
    private ApiCallMapper apiCallMapper;

    @Override
    public Long getOnlineApiCnt(Long workspaceId) {
        return apiConfigMapper.getOnlineApiCnt(workspaceId);
    }

    @Override
    public Long getOfflineApiCnt(Long workspaceId) {
        return apiConfigMapper.getOfflineApiCnt(workspaceId);
    }

    @Override
    public long getCallTotalCnt(CallMonitorResquest callMonitorResquest) {
        return apiCallMapper.getCallTotalCnt(callMonitorResquest);
    }

    @Override
    public long getCallTotalTime(CallMonitorResquest callMonitorResquest) {
        return apiCallMapper.getCallTotalTime(callMonitorResquest);
    }

    @Override
    public List<ApiCallInfoByCnt> getCallListByCnt(CallMonitorResquest callMonitorResquest) {
        return apiCallMapper.getCallListByCnt(callMonitorResquest);
    }

    @Override
    public List<ApiCallInfoByFailRate> getCallListByFailRate(CallMonitorResquest callMonitorResquest) {
        return apiCallMapper.getCallListByFailRate(callMonitorResquest);
    }
}
