package com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity;

import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.OrchestrationToRelConversionRequestRef;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.WorkflowPreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.WorkflowPreConversionRelImpl;

public class DolphinSchedulerConvertedRel extends WorkflowPreConversionRelImpl implements ConvertedRel {

    public DolphinSchedulerConvertedRel(PreConversionRel rel) {
        setWorkflow(((WorkflowPreConversionRel) rel).getWorkflow());
        setDSSToRelConversionRequestRef(rel.getDSSToRelConversionRequestRef());
    }

    @Override
    public OrchestrationToRelConversionRequestRef getDSSToRelConversionRequestRef() {
        return (OrchestrationToRelConversionRequestRef) super.getDSSToRelConversionRequestRef();
    }

}
