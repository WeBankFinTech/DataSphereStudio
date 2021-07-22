package com.webank.wedatasphere.dss.framework.dbapi.service;

import com.webank.wedatasphere.dss.framework.dbapi.entity.request.CallMonitorResquest;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiCallInfoByCnt;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiCallInfoByFailRate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    long getCallTotalCnt(@Param("workspaceId") Long workspaceId);

    /**
     * 总执行时长
     */
    long getCallTotalTime(@Param("workspaceId") Long workspaceId);

    /**
     * 出错率排行TOP10
     */
    List<ApiCallInfoByCnt> getCallListByCnt(CallMonitorResquest callMonitorResquest);

    /**
     * 调用量排行TOP10
     */
    List<ApiCallInfoByFailRate> getCallListByFailRate(CallMonitorResquest callMonitorResquest);



}
