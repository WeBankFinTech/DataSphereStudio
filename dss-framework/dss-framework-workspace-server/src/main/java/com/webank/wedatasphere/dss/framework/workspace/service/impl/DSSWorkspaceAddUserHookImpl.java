package com.webank.wedatasphere.dss.framework.workspace.service.impl;

import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceAddUserHook;

/**
 * Author: xlinliu
 * Date: 2023/6/30
 */
public class DSSWorkspaceAddUserHookImpl implements DSSWorkspaceAddUserHook {
    @Override
    public void beforeAdd(String userName, long workspaceId) {
        //do nothing
    }

    @Override
    public void afterAdd(String userName, long workspaceId) {
        // do nothing
    }
}
