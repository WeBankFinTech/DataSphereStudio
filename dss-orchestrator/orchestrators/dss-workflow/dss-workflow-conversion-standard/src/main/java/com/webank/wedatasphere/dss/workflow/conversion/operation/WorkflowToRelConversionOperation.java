package com.webank.wedatasphere.dss.workflow.conversion.operation;

import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.DSSToRelConversionRequestRef;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.WorkflowPreConversionRelImpl;
import com.webank.wedatasphere.dss.workflow.core.WorkflowFactory;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;

/**
 * @author enjoyyin
 * @date 2022-03-16
 * @since 0.5.0
 */
public class WorkflowToRelConversionOperation
        extends AbstractDSSToRelConversionOperation<DSSToRelConversionRequestRef.OrchestrationToRelConversionRequestRefImpl> {

    @Override
    protected PreConversionRel getPreConversionRel(DSSToRelConversionRequestRef.OrchestrationToRelConversionRequestRefImpl ref) {
        Workflow workflow = WorkflowFactory.INSTANCE.getJsonToFlowParser().parse((DSSFlow) ref.getDSSOrchestration());
        WorkflowPreConversionRelImpl rel = new WorkflowPreConversionRelImpl();
        rel.setDSSToRelConversionRequestRef(ref);
        rel.setWorkflow(workflow);
        return rel;
    }

}
