package com.webank.wedatasphere.dss.framework.dbapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiAuthMapper;
import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiConfigMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiAuth;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.framework.dbapi.service.ApiAuthService;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import lombok.extern.slf4j.Slf4j;
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
    public List<ApiAuth> getApiAuthList(Long workspaceId, List<Long> totals, Integer pageNow, Integer pageSize){
        PageHelper.startPage(pageNow, pageSize, true);
        List<ApiAuth> apiAuthList = apiAuthMapper.getApiAuthList(workspaceId);
        PageInfo<ApiAuth> pageInfo = new PageInfo<>(apiAuthList);
        totals.add(pageInfo.getTotal());

        return apiAuthList;
    }

    @Override
    public void deleteApiAuth(Long id){
        apiAuthMapper.deleteApiAuth(id);
        log.info("-------delete apiauth:    " + id + ", ok");
    }

    @Override
    public List<ApiGroupInfo> getApiGroupList(Long workspaceId){
        return apiConfigMapper.getGroupByWorkspaceId(workspaceId.toString());
    }
}
