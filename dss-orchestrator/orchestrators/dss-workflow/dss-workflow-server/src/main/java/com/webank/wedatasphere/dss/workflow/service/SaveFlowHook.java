package com.webank.wedatasphere.dss.workflow.service;

import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;

/**
 * Author: xlinliu
 * Date: 2023/7/28
 */
public interface SaveFlowHook {
    void beforeSave(String jsonFlow, DSSFlow dssFlow,Long parentFlowID);
    void afterSave(String jsonFlow, DSSFlow dssFlow,Long parentFlowID);
}
