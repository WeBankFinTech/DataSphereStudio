package com.webank.wedatasphere.dss.data.api.server.service.impl;

import com.webank.wedatasphere.dss.data.api.server.dao.ApiCallMapper;
import com.webank.wedatasphere.dss.data.api.server.dao.ApiConfigMapper;
import com.webank.wedatasphere.dss.data.api.server.entity.request.CallMonitorResquest;
import com.webank.wedatasphere.dss.data.api.server.entity.request.SingleCallMonitorRequest;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiCallInfoByCnt;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiCallInfoByFailRate;
import com.webank.wedatasphere.dss.data.api.server.entity.response.HourMonitorInfo;
import com.webank.wedatasphere.dss.data.api.server.service.ApiMonitorService;
import com.webank.wedatasphere.dss.data.api.server.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<HourMonitorInfo> getCallCntForPast24H(Long workspaceId) throws Exception {
        //整点小时的时间点集合
        ArrayList<String> timeTagList = TimeUtil.getTimeLagForPast24H();
        //小时整点-次数（很可能数据不全，会有数据缺失）
        List<Map<String, Object>> oldLMap = apiCallMapper.getCallCntForPast24H(workspaceId);
        Map<String, Object> oldMap =transforLMap2Map(oldLMap);

        List<HourMonitorInfo> list = new ArrayList<>();
        for (String timeTag: timeTagList) {
            list.add(new HourMonitorInfo(timeTag,oldMap.getOrDefault(timeTag,0)));
        }

        return list;
    }

    @Override
    public List<HourMonitorInfo> getCallTimeForSinleApi(SingleCallMonitorRequest singleCallMonitorRequest) throws Exception {
        //整点小时的时间点集合
        ArrayList<String> timeTagList = TimeUtil.getTimeLag(singleCallMonitorRequest.getStartTime(),singleCallMonitorRequest.getEndTime());
        //小时整点-次数（很可能数据不全，会有数据缺失）
        List<Map<String, Object>> oldLMap = apiCallMapper.getCallTimeForSinleApi(singleCallMonitorRequest);
        Map<String, Object> oldMap =transforLMap2Map(oldLMap);

        List<HourMonitorInfo> list = new ArrayList<>();
        for (String timeTag: timeTagList) {
            list.add(new HourMonitorInfo(timeTag,oldMap.getOrDefault(timeTag,0)));
        }

        return list;
    }

    @Override
    public List<HourMonitorInfo> getCallCntForSinleApi(SingleCallMonitorRequest singleCallMonitorRequest) throws Exception {
        //整点小时的时间点集合
        ArrayList<String> timeTagList = TimeUtil.getTimeLag(singleCallMonitorRequest.getStartTime(),singleCallMonitorRequest.getEndTime());
        //小时整点-次数（很可能数据不全，会有数据缺失）
        List<Map<String, Object>> oldLMap = apiCallMapper.getCallCntForSinleApi(singleCallMonitorRequest);
        Map<String, Object> oldMap =transforLMap2Map(oldLMap);

        List<HourMonitorInfo> list = new ArrayList<>();
        for (String timeTag: timeTagList) {
            list.add(new HourMonitorInfo(timeTag,oldMap.getOrDefault(timeTag,0)));
        }

        return list;
    }


    /**
     * 转换数据格式
     */
    private Map<String, Object> transforLMap2Map(List<Map<String, Object>> lMap){
        Map<String, Object> newMap = new HashMap<>();

        for (Map<String, Object> map: lMap) {
            String key =null;
            Object value =null;
            for (Map.Entry<String, Object> maps: map.entrySet()) {
                //System.out.println("key"+maps.getKey());
                //System.out.println("key"+maps.getValue());
                if("k".equals(maps.getKey())){
                    key = String.valueOf(maps.getValue());
                }
                if("v".equals(maps.getKey())){
                    value = String.valueOf(maps.getValue());
                }
            }
            newMap.put(key,value);
        }
        return newMap ;
    }
}
