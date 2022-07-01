package com.webank.wedatasphere.dss.workflow.conversion.entity;

import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.DSSToRelConversionRequestRef;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;

/**
 * @author enjoyyin
 * @date 2022-03-16
 * @since 0.5.0
 */
public class WorkflowPreConversionRelImpl implements WorkflowPreConversionRel {

    private DSSToRelConversionRequestRef dssToRelConversionRequestRef;
    private Workflow workflow;

    @Override
    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    @Override
    public DSSToRelConversionRequestRef getDSSToRelConversionRequestRef() {
        return dssToRelConversionRequestRef;
    }

    public void setDSSToRelConversionRequestRef(DSSToRelConversionRequestRef dssToRelConversionRequestRef) {
        this.dssToRelConversionRequestRef = dssToRelConversionRequestRef;
    }
}
