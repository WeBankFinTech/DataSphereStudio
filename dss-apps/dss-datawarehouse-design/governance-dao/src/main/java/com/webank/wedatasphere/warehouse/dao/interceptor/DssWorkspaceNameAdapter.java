package com.webank.wedatasphere.warehouse.dao.interceptor;

import org.apache.linkis.common.conf.CommonVars;

public class DssWorkspaceNameAdapter {

    public static final CommonVars<String> BDP_ENTITY_WORKSPACE_NAME_AUTO_TRANSFORM = CommonVars.apply("wds.entity.workspace.name.auto.transform", "false");

    public String getWorkspaceName() {
        // TODO 真正获取当前用户线程的工作空间名称
        return "workspaceName";
    }

}
