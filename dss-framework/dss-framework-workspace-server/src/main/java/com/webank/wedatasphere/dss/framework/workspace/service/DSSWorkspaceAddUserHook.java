package com.webank.wedatasphere.dss.framework.workspace.service;

import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

/**
 * Author: xlinliu
 * Date: 2023/6/30
 */
public interface DSSWorkspaceAddUserHook {
    /**
     * 加入工作空间前执行
     */
    void beforeAdd(String userName, long workspaceId);

    /**
     * 加入工作空间后执行
     */

    void afterAdd(String userName, long workspaceId);
}
