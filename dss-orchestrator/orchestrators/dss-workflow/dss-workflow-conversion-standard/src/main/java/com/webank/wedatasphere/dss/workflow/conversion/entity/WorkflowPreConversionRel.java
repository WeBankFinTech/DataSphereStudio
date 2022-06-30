package com.webank.wedatasphere.dss.workflow.conversion.entity;

import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;

/**
 * @author enjoyyin
 * @date 2022-03-16
 * @since 0.5.0
 */
public interface WorkflowPreConversionRel extends PreConversionRel {

    Workflow getWorkflow();

}
