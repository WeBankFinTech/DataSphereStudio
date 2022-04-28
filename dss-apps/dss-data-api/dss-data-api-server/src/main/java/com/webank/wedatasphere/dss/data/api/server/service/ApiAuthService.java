package com.webank.wedatasphere.dss.data.api.server.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.data.api.server.entity.ApiAuth;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiAuthInfo;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiGroupInfo;
import org.apache.linkis.common.exception.ErrorException;

import java.util.List;

public interface ApiAuthService extends IService<ApiAuth> {
    public List<ApiGroupInfo> getApiGroupList(Long workspaceId);
    public boolean saveApiAuth(ApiAuth apiAuth) throws ErrorException;

    public List<ApiAuthInfo> getApiAuthList(Long workspaceId, String caller, List<Long> totals, Integer pageNow, Integer pageSize);

    public void deleteApiAuth(Long id);


}
