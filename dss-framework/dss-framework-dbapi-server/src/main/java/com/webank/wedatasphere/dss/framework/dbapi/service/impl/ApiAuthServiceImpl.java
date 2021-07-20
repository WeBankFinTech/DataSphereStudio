package com.webank.wedatasphere.dss.framework.dbapi.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiAuthMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiAuth;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiInfo;
import com.webank.wedatasphere.dss.framework.dbapi.service.ApiAuthService;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname DSSDataApiAuthServiceImpl
 * @Description TODO
 * @Date 2021/7/13 17:13
 * @Created by suyc
 */
@Service
public class ApiAuthServiceImpl implements ApiAuthService {
    @Autowired
    private ApiAuthMapper apiAuthMapper;

    @Override
    public Long createApiAuth(ApiAuth apiAuth) throws ErrorException {
        apiAuthMapper.insert(apiAuth);
        return apiAuth.getId();
    }

    @Override
    public List<ApiAuth> getApiAuthList(Long workspaceId, List<Long> totals, Integer pageNow, Integer pageSize){
        PageHelper.startPage(pageNow, pageSize, true);
        List<ApiAuth> apiAuthList = apiAuthMapper.getApiAuthList(workspaceId);
        PageInfo<ApiAuth> pageInfo = new PageInfo<>(apiAuthList);
        totals.add(pageInfo.getTotal());

        return apiAuthList;
    }

    @Override
    public List<ApiInfo> getApiInfoList(Long workspaceId, List<Long> totals, Integer pageNow, Integer pageSize){
        PageHelper.startPage(pageNow,pageSize,true);
        List<ApiInfo> apiInfoList = apiAuthMapper.getApiInfoList(workspaceId);
        PageInfo<ApiInfo> pageInfo = new PageInfo<>(apiInfoList);
        totals.add(pageInfo.getTotal());

        return apiInfoList;
    }

}
