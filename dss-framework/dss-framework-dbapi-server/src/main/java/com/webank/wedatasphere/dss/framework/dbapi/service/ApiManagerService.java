package com.webank.wedatasphere.dss.framework.dbapi.service;

import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiInfo;

import java.util.List;

/**
 * @Classname ApiManagerService
 * @Description TODO
 * @Date 2021/7/22 20:51
 * @Created by suyc
 */
public interface ApiManagerService {
    public List<ApiInfo> getApiInfoList(Long workspaceId, String apiName, List<Long> totals, Integer pageNow, Integer pageSize);
    public List<ApiInfo> getOnlineApiInfoList(Long workspaceId, String apiName, List<Long> totals, Integer pageNow, Integer pageSize);

    public void offlineApi(Long apiId);

    public void onlineApi(Long apiId);
}
