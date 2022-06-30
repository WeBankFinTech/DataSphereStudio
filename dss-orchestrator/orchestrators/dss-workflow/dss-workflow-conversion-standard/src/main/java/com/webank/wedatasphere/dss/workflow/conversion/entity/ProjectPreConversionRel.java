package com.webank.wedatasphere.dss.workflow.conversion.entity;

import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;

import java.util.List;

/**
 * @author enjoyyin
 * @date 2022-03-16
 * @since 0.5.0
 */
public interface ProjectPreConversionRel extends PreConversionRel {

    List<Workflow> getWorkflows();

}
