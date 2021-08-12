package com.webank.wedatasphere.dss.framework.dbapi.service;

import com.webank.wedatasphere.dss.framework.dbapi.entity.request.CallMonitorResquest;
import com.webank.wedatasphere.dss.framework.dbapi.entity.request.SingleCallMonitorRequest;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiCallInfoByCnt;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiCallInfoByFailRate;

import java.util.List;
import java.util.Map;

/**
 * @Classname ApiMonitorService
 * @Description TODO
 * @Date 2021/7/20 11:59
 * @Created by suyc
 */
public interface ApiMonitorService {
    /**
     * 已发布API数量
     */
    Long getOnlineApiCnt(Long workspaceId);

    /**
     * 未发布API数量
     */
    Long getOfflineApiCnt(Long workspaceId);

    /**
     * 总调用次数
     */
    Long getCallTotalCnt(CallMonitorResquest callMonitorResquest);

    /**
     * 总执行时长
     */
    Long getCallTotalTime(CallMonitorResquest callMonitorResquest);

    /**
     * 出错率排行TOP10
     */
    List<ApiCallInfoByCnt> getCallListByCnt(CallMonitorResquest callMonitorResquest);

    /**
     * 调用量排行TOP10
     */
    List<ApiCallInfoByFailRate> getCallListByFailRate(CallMonitorResquest callMonitorResquest);

    /**
     * 过去24小时，每小时的请求数目
     */
    List<Map<String, Object>> getCallCntForPast24H(Long workspaceId);

    /**
     * 时间范围内指定API的每小时的平均响应时间
     */
    List<Map<String, Object>> getCallTimeForSinleApi(SingleCallMonitorRequest singleCallMonitorRequest);

    /**
     * 时间范围内指定API的每小时的请求次数
     */
    List<Map<String, Object>> getCallCntForSinleApi(SingleCallMonitorRequest singleCallMonitorRequest);
}
