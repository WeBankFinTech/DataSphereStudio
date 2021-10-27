package com.webank.wedatasphere.dss.appconn.dolphinscheduler.standard;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerWorkflowService;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.DSSToRelConversionService;
import com.webank.wedatasphere.dss.workflow.conversion.WorkflowConversionIntegrationStandard;

/**
 * The type Dolphin scheduler workflow standard.
 *
 * @author yuxin.yuan
 * @date 2021/10/27
 */
public class DolphinSchedulerWorkflowStandard extends WorkflowConversionIntegrationStandard {

    @Override
    protected DSSToRelConversionService createDSSToRelConversionService() {
        return new DolphinSchedulerWorkflowService();
    }

}
