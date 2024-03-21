package com.webank.wedatasphere.dss.workflow.service.impl;

import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.service.SaveFlowHook;

/**
 * Author: xlinliu
 * Date: 2023/7/28
 */
public class SaveFlowHookImpl implements SaveFlowHook {
    @Override
    public void beforeSave(String jsonFlow, DSSFlow dssFlow, Long parentFlowID) {
        //do nothing
    }

    @Override
    public void afterSave(String jsonFlow, DSSFlow dssFlow, Long parentFlowID) {
        //do nothing
    }
}
