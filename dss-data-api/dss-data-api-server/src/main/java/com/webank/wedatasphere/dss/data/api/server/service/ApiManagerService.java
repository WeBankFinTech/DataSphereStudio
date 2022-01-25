package com.webank.wedatasphere.dss.data.api.server.service;


import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiInfo;

import java.util.List;

/**
 * @Classname ApiManagerService
 * @Description TODO
 * @Date 2021/7/22 20:51
 * @Created by suyc
 */
public interface ApiManagerService {
    //public List<ApiGroup> getApiGroupList(Long workspaceId);

    public ApiInfo getApiInfo(Long apiId);
    public List<ApiInfo> getApiInfoList(Long workspaceId, String apiName, List<Long> totals, Integer pageNow, Integer pageSize);
    public List<ApiInfo> getOnlineApiInfoList(Long workspaceId, String apiName, List<Long> totals, Integer pageNow, Integer pageSize);

    public void offlineApi(Long apiId);

    public void onlineApi(Long apiId);
}
