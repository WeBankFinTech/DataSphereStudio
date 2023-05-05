package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.webank.wedatasphere.dss.framework.workspace.dao.DSSWorkspaceMapper;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceRoleCheckService;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DSSWorkspaceRoleCheckServiceImpl implements DSSWorkspaceRoleCheckService {

    @Autowired
    private DSSWorkspaceMapper dssWorkspaceMapper;

    @Autowired
    DSSWorkspaceService dssWorkspaceService;

    @Override
    public boolean checkRolesOperation(int workspaceId, String loginUser, String username, List<Integer> roles) {
        // 获取工作空间创建者
        String createBy = dssWorkspaceMapper.getWorkspace(workspaceId).getCreateBy();
        return StringUtils.equals(loginUser, createBy) ?
                (!StringUtils.equals(createBy, username) || roles.contains(1)) :
                (dssWorkspaceService.isAdminUser((long) workspaceId, loginUser) && ((!dssWorkspaceService.isAdminUser((long) workspaceId, username) && !roles.contains(1))
                        || (StringUtils.equals(loginUser, username) && roles.contains(1))));
    }
}
