package com.webank.wedatasphere.dss.orchestrator.db.hook;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;

import java.util.Map;

/**
 * Author: xlinliu
 * Date: 2023/8/1
 */
public interface AddOrchestratorVersionHook {
    void beforeAdd(DSSOrchestratorVersion oldVersion, Map<String,Object> content);
    void afterAdd(DSSOrchestratorVersion newVersion, Map<String,Object> content);
}
