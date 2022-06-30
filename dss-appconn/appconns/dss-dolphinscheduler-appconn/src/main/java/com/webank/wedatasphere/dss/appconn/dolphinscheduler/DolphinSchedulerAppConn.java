package com.webank.wedatasphere.dss.appconn.dolphinscheduler;

import com.webank.wedatasphere.dss.appconn.core.ext.OptionalAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.standard.DolphinSchedulerStructureStandard;
import com.webank.wedatasphere.dss.appconn.scheduler.AbstractSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerStructureIntegrationStandard;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ConversionIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.OptionalIntegrationStandard;
import com.webank.wedatasphere.dss.workflow.conversion.WorkflowConversionIntegrationStandard;

public class DolphinSchedulerAppConn extends AbstractSchedulerAppConn implements OptionalAppConn {

    public static final String DOLPHINSCHEDULER_APPCONN_NAME = "dolphinscheduler";

    @Override
    public ConversionIntegrationStandard createConversionIntegrationStandard() {
        if (super.getOrCreateConversionStandard() == null) {
            return new WorkflowConversionIntegrationStandard();
        } else {
            return super.getOrCreateConversionStandard();
        }
    }

    @Override
    public SchedulerStructureIntegrationStandard getOrCreateStructureStandard() {
        return DolphinSchedulerStructureStandard.getInstance();
    }

    @Override
    public OptionalIntegrationStandard getOrCreateOptionalStandard() {
        return OptionalAppConn.super.getOrCreateOptionalStandard();
    }
}
