package com.webank.wedatasphere.dss.data.api.server.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.data.api.server.entity.ApiAuth;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiAuthInfo;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

import java.util.List;

/**
 * @Classname DSSDataApiAuthService
 * @Description TODO
 * @Date 2021/7/13 17:10
 * @Created by suyc
 */
public interface ApiAuthService extends IService<ApiAuth> {
    public List<ApiGroupInfo> getApiGroupList(Long workspaceId);
    public boolean saveApiAuth(ApiAuth apiAuth) throws ErrorException;

    public List<ApiAuthInfo> getApiAuthList(Long workspaceId, List<Long> totals, Integer pageNow, Integer pageSize);

    public void deleteApiAuth(Long id);


}
