package com.webank.wedatasphere.dss.appconn.dolphinscheduler;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.standard.DolphinSchedulerStructureStandard;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.standard.DolphinSchedulerWorkflowStandard;
import com.webank.wedatasphere.dss.appconn.scheduler.AbstractSchedulerAppConn;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.workflow.conversion.WorkflowConversionIntegrationStandard;

/**
 * The type Dolphin scheduler app conn.
 *
 * @author yuxin.yuan
 * @date 2021/10/18
 */
public class DolphinSchedulerAppConn extends AbstractSchedulerAppConn {

    public static final String DOLPHINSCHEDULER_APPCONN_NAME = "dolphinscheduler";

    private DolphinSchedulerWorkflowStandard dolphinSchedulerWorkflowStandard;

    @Override
    protected void initialize() {
        dolphinSchedulerWorkflowStandard = new DolphinSchedulerWorkflowStandard();
    }

    @Override
    public WorkflowConversionIntegrationStandard getOrCreateWorkflowConversionStandard() {
        dolphinSchedulerWorkflowStandard.setSSORequestService(this.getOrCreateSSOStandard().getSSORequestService());
        dolphinSchedulerWorkflowStandard.setAppConnName(getAppDesc().getAppName());
        return dolphinSchedulerWorkflowStandard;
    }

    @Override
    public StructureIntegrationStandard getOrCreateStructureStandard() {
        return DolphinSchedulerStructureStandard.getInstance();
    }

}
