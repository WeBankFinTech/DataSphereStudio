package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerWorkflowUploadOperation;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.AbstractConversionService;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.DSSToRelConversionService;
import com.webank.wedatasphere.dss.workflow.conversion.operation.WorkflowToRelConversionOperation;

/**
 * The type Dolphin scheduler ref scheduler service.
 *
 * @author yuxin.yuan
 * @date 2021/05/21
 */
public class DolphinSchedulerWorkflowService extends AbstractConversionService implements DSSToRelConversionService {

    public WorkflowToRelConversionOperation getDSSToRelConversionOperation() {
        return getOrCreate(DolphinSchedulerWorkflowUploadOperation::new, DolphinSchedulerWorkflowUploadOperation.class);
    }

    @Override
    public boolean isConvertAllOrcs() {
        return false;
    }

}
