package com.webank.wedatasphere.dss.framework.dbapi.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiConfigMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiInfo;
import com.webank.wedatasphere.dss.framework.dbapi.service.ApiManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname ApiManagerServiceImpl
 * @Description TODO
 * @Date 2021/7/22 20:52
 * @Created by suyc
 */
@Service
public class ApiManagerServiceImpl implements ApiManagerService {
    @Autowired
    private ApiConfigMapper apiConfigMapper;

    @Override
    public List<ApiInfo> getApiInfoList(Long workspaceId, String apiName, List<Long> totals, Integer pageNow, Integer pageSize){
        PageHelper.startPage(pageNow,pageSize,true);
        List<ApiInfo> apiInfoList = apiConfigMapper.getApiInfoList(workspaceId,apiName);
        PageInfo<ApiInfo> pageInfo = new PageInfo<>(apiInfoList);
        totals.add(pageInfo.getTotal());

        return apiInfoList;
    }

    @Override
    public List<ApiInfo> getOnlineApiInfoList(Long workspaceId, String apiName, List<Long> totals, Integer pageNow, Integer pageSize){
        PageHelper.startPage(pageNow,pageSize,true);
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
