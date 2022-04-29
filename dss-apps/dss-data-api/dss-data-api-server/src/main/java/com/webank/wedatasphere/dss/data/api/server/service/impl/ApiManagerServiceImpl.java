package com.webank.wedatasphere.dss.data.api.server.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.data.api.server.dao.ApiConfigMapper;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiInfo;
import com.webank.wedatasphere.dss.data.api.server.service.ApiManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiManagerServiceImpl implements ApiManagerService {
    @Autowired
    private ApiConfigMapper apiConfigMapper;

    @Override
    public ApiInfo getApiInfo(Long apiId){
        return apiConfigMapper.getApiInfo(apiId);
    }

    @Override
    public List<ApiInfo> getApiInfoList(Long workspaceId, String apiName, List<Long> totals, Integer pageNow, Integer pageSize){
        PageHelper.startPage(pageNow,pageSize,true);
        // MYSQL LIKE % _:  LIKE '%\_%', LIKE '%\%%'
        if(apiName !=null) {
            if ("_".equalsIgnoreCase(apiName.trim())) {
                apiName = "\\_";
            }
            if ("%".equalsIgnoreCase(apiName.trim())) {
                apiName = "\\%";
            }
        }
        List<ApiInfo> apiInfoList = apiConfigMapper.getApiInfoList(workspaceId,apiName);
        PageInfo<ApiInfo> pageInfo = new PageInfo<>(apiInfoList);
        totals.add(pageInfo.getTotal());

        return apiInfoList;
    }

    @Override
    public List<ApiInfo> getOnlineApiInfoList(Long workspaceId, String apiName, List<Long> totals, Integer pageNow, Integer pageSize){
        PageHelper.startPage(pageNow,pageSize,true);
        // MYSQL LIKE % _:  LIKE '%\_%', LIKE '%\%%'
        if(apiName !=null) {
            if ("_".equalsIgnoreCase(apiName.trim())) {
                apiName = "\\_";
            }
            if ("%".equalsIgnoreCase(apiName.trim())) {
                apiName = "\\%";
            }
        }
        List<ApiInfo> apiInfoList = apiConfigMapper.getOnlineApiInfoList(workspaceId,apiName);
        PageInfo<ApiInfo> pageInfo = new PageInfo<>(apiInfoList);
        totals.add(pageInfo.getTotal());

        return apiInfoList;
    }

    @Override
    public void offlineApi(Long apiId){
        apiConfigMapper.offlineApi(apiId);
    }

    @Override
    public void onlineApi(Long apiId){
        apiConfigMapper.onlineApi(apiId);
    }
}
