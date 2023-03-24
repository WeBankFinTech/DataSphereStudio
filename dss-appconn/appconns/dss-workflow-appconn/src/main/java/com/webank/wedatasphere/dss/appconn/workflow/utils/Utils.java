package com.webank.wedatasphere.dss.appconn.workflow.utils;

import com.webank.wedatasphere.dss.workflow.DefaultWorkFlowManager;
import com.webank.wedatasphere.dss.workflow.WorkFlowManager;
import org.apache.linkis.DataWorkCloudApplication;

public class Utils {
    public static WorkFlowManager getDefaultWorkflowManager() {
        return DataWorkCloudApplication.getApplicationContext().getBean(DefaultWorkFlowManager.class);
    }
}
