package com.webank.wedatasphere.dss.data.api.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.webank.wedatasphere.dss.data.api.server.dao.ApiAuthMapper;
import com.webank.wedatasphere.dss.data.api.server.dao.ApiConfigMapper;
import com.webank.wedatasphere.dss.data.api.server.entity.ApiAuth;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiAuthInfo;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.data.api.server.service.ApiAuthService;
import org.apache.linkis.common.exception.ErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ApiAuthServiceImpl extends ServiceImpl<ApiAuthMapper, ApiAuth> implements ApiAuthService {
    @Autowired
    private ApiAuthMapper apiAuthMapper;
    @Autowired
    private ApiConfigMapper apiConfigMapper;

    @Override
    public boolean saveApiAuth(ApiAuth apiAuth) throws ErrorException {
        Long id = apiAuth.getId();

        if(id != null){
            return this.updateById(apiAuth);
        }
        else {
            return this.save(apiAuth);
        }
    }

    @Override
    public List<ApiAuthInfo> getApiAuthList(Long workspaceId, String caller, List<Long> totals, Integer pageNow, Integer pageSize){
        PageHelper.startPage(pageNow, pageSize, true);
        // MYSQL LIKE % _:  LIKE '%\_%', LIKE '%\%%'
        if(caller !=null) {
            if ("_".equalsIgnoreCase(caller.trim())) {
                caller = "\\_";
            }
            if ("%".equalsIgnoreCase(caller.trim())) {
                caller = "\\%";
            }
        }
        List<ApiAuthInfo> apiAuthList = apiAuthMapper.getApiAuthList(workspaceId,caller);
        PageInfo<ApiAuthInfo> pageInfo = new PageInfo<>(apiAuthList);
        totals.add(pageInfo.getTotal());

        return apiAuthList;
    }

    @Override
    public void deleteApiAuth(Long id){
        apiAuthMapper.deleteApiAuth(id);
    }

    @Override
    public List<ApiGroupInfo> getApiGroupList(Long workspaceId){
        return apiConfigMapper.getGroupByWorkspaceId(workspaceId.toString());
    }
}
