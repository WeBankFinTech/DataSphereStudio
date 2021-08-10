package com.webank.wedatasphere.dss.framework.dbapi.service.impl;

import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiCallMapper;
import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiConfigMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.request.CallMonitorResquest;
import com.webank.wedatasphere.dss.framework.dbapi.entity.request.SingleCallMonitorRequest;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiCallInfoByCnt;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiCallInfoByFailRate;
import com.webank.wedatasphere.dss.framework.dbapi.service.ApiMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    public Long getCallTotalCnt(CallMonitorResquest callMonitorResquest) {
        return apiCallMapper.getCallTotalCnt(callMonitorResquest);
    }

    @Override
    public Long getCallTotalTime(CallMonitorResquest callMonitorResquest) {
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

    @Override
    public List<Map<String, Object>> getCallCntForPast24H(Long workspaceId) {
        return apiCallMapper.getCallCntForPast24H(workspaceId);
    }

    @Override
    public List<Map<String, Object>> getCallTimeForSinleApi(SingleCallMonitorRequest singleCallMonitorRequest) {
        return apiCallMapper.getCallTimeForSinleApi(singleCallMonitorRequest);
    }

    @Override
    public List<Map<String, Object>> getCallCntForSinleApi(SingleCallMonitorRequest singleCallMonitorRequest) {
        return apiCallMapper.getCallCntForSinleApi(singleCallMonitorRequest);
    }
}
