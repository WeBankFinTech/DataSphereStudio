package com.webank.wedatasphere.dss.framework.dbapi.service;

/**
 * @Classname ApiMonitorService
 * @Description TODO
 * @Date 2021/7/20 11:59
 * @Created by suyc
 */
public interface ApiMonitorService {
    //已发布API数量
    public Long getOnlineApiCnt(Long workspaceId);

    //未发布API数量
    public Long getOfflineApiCnt(Long workspaceId);

    //总调用次数

    //总执行时长

    //出错排行TOP10

    //调用量排行TOP10


    //
}
