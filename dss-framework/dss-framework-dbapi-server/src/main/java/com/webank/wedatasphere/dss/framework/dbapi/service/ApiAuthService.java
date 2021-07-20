package com.webank.wedatasphere.dss.framework.dbapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiAuth;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiInfo;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

import java.util.List;

/**
 * @Classname DSSDataApiAuthService
 * @Description TODO
 * @Date 2021/7/13 17:10
 * @Created by suyc
 */
public interface ApiAuthService extends IService<ApiAuth> {
    public Long createApiAuth(ApiAuth apiAuth) throws ErrorException;

    public List<ApiAuth> getApiAuthList(Long workspaceId, List<Long> totals, Integer pageNow, Integer pageSize);

    public void deleteApiAuth(Long id);




    public List<ApiInfo> getApiInfoList(Long workspaceId, List<Long> totals, Integer pageNow, Integer pageSize);

    public void offlineApi(Long apiId);

    public void onlineApi(Long apiId);
}
