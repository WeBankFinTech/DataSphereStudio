package com.webank.wedatasphere.dss.framework.workspace.service;

import java.util.List;

public interface DSSWorkspaceRoleCheckService {

    /**
     * 权限管理界面判断登录人是否拥有用户操作权限
     * @param workspaceId  工作空间id
     * @param loginUser  当前登录用户
     * @param username  将要修改的用户
     * @param roles  将要修改的角色
     * @return
     */
    boolean checkRolesOperation(int workspaceId, String loginUser, String username, List<Integer> roles);

}
