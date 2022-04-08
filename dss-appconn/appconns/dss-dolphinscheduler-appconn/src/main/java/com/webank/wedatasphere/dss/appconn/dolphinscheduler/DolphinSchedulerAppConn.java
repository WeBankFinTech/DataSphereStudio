package com.webank.wedatasphere.dss.appconn.dolphinscheduler;

import com.webank.wedatasphere.dss.appconn.core.ext.OptionalAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.standard.DolphinSchedulerStructureStandard;
import com.webank.wedatasphere.dss.appconn.scheduler.AbstractSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerStructureIntegrationStandard;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ConversionIntegrationStandard;
import com.webank.wedatasphere.dss.workflow.conversion.WorkflowConversionIntegrationStandard;

public class DolphinSchedulerAppConn extends AbstractSchedulerAppConn implements OptionalAppConn {

    public static final String DOLPHINSCHEDULER_APPCONN_NAME = "dolphinscheduler";

    @Override
    protected ConversionIntegrationStandard createConversionIntegrationStandard() {
        return new WorkflowConversionIntegrationStandard();
    }

    @Override
    public SchedulerStructureIntegrationStandard getOrCreateStructureStandard() {
        return DolphinSchedulerStructureStandard.getInstance();
    }

}
