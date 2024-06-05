package com.webank.wedatasphere.dss.orchestrator.db.hook;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;

import java.util.Map;

/**
 * Author: xlinliu
 * Date: 2023/8/1
 */
public class AddOrchestratorVersionHookDefaultImpl implements AddOrchestratorVersionHook{
    @Override
    public void beforeAdd(DSSOrchestratorVersion oldVersion, Map<String, Object> content) {
        // default impl do nothing
    }

    @Override
    public void afterAdd(DSSOrchestratorVersion newVersion, Map<String, Object> content) {
        // default impl do nothing
    }
}
