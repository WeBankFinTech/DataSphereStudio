package com.webank.wedatasphere.dss.appconn.dolphinscheduler;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.standard.DolphinSchedulerStructureStandard;
import com.webank.wedatasphere.dss.appconn.scheduler.AbstractSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerStructureIntegrationStandard;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ConversionIntegrationStandard;
import com.webank.wedatasphere.dss.workflow.conversion.WorkflowConversionIntegrationStandard;

public class DolphinSchedulerAppConn extends AbstractSchedulerAppConn {

    public static final String DOLPHINSCHEDULER_APPCONN_NAME = "dolphinscheduler";

    private WorkflowConversionIntegrationStandard dolphinSchedulerWorkflowStandard;

    @Override
    protected void initialize() {
        dolphinSchedulerWorkflowStandard = new WorkflowConversionIntegrationStandard();
    }

    @Override
    public ConversionIntegrationStandard getOrCreateConversionStandard() {
        dolphinSchedulerWorkflowStandard.setSSORequestService(this.getOrCreateSSOStandard().getSSORequestService());
        dolphinSchedulerWorkflowStandard.setAppConn(this);
        dolphinSchedulerWorkflowStandard.init();
        return dolphinSchedulerWorkflowStandard;
    }

    @Override
    public SchedulerStructureIntegrationStandard getOrCreateStructureStandard() {
        return DolphinSchedulerStructureStandard.getInstance();
    }

}
