package com.webank.wedatasphere.dss.framework.release.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.dss.framework.release.dao.ReleaseServerDSSProjectUserMapper;
import com.webank.wedatasphere.dss.framework.release.entity.project.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.framework.release.entity.project.ReleaseServerDSSProjectUser;
import com.webank.wedatasphere.dss.framework.release.service.ReleaseServerDSSProjectUserService;

@Service
public class ReleaseServerDSSProjectUserServiceImpl implements ReleaseServerDSSProjectUserService {

    @Autowired
    private ReleaseServerDSSProjectUserMapper projectUserMapper;

    @Override
    public boolean isPublishAuth(Long projectId, String username) {
        QueryWrapper<ReleaseServerDSSProjectUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        queryWrapper.eq("username", username);
        queryWrapper.ge("priv", ProjectUserPrivEnum.PRIV_RELEASE.getRank());// 发布权限
        long count = projectUserMapper.selectCount(queryWrapper);
        return count != 0;
    }

}
