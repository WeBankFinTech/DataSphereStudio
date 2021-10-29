package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerProcessDefinitionQueryOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerWorkflowUploadOperation;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.AbstractConversionService;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.DSSToRelConversionService;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.service.Operation;
import com.webank.wedatasphere.dss.workflow.conversion.operation.WorkflowToRelConversionOperation;

/**
 * The type Dolphin scheduler ref scheduler service.
 *
 * @author yuxin.yuan
 * @date 2021/05/21
 */
public class DolphinSchedulerWorkflowService extends AbstractConversionService implements DSSToRelConversionService {

    private Map<Class<? extends Operation>, Operation<?, ?>> operationMap = new ConcurrentHashMap<>();

    public DolphinSchedulerWorkflowService(AppInstance appInstance) {
        operationMap.put(RefQueryOperation.class,
            new DolphinSchedulerProcessDefinitionQueryOperation(appInstance.getBaseUrl()));
    }

    @Override
    public WorkflowToRelConversionOperation getDSSToRelConversionOperation() {
        return getOrCreate(DolphinSchedulerWorkflowUploadOperation::new, DolphinSchedulerWorkflowUploadOperation.class);
    }

    @Override
    public boolean isConvertAllOrcs() {
        return false;
    }

    @Override
    public Operation createOperation(Class<? extends Operation> clazz) {
        return this.operationMap.get(clazz);
    }

}
