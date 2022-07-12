package com.webank.wedatasphere.dss.workflow.conversion.service;

import com.webank.wedatasphere.dss.orchestrator.converter.standard.operation.DSSToRelConversionOperation;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.AbstractConversionService;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.DSSToRelConversionService;
import com.webank.wedatasphere.dss.workflow.conversion.operation.ProjectToRelConversionOperation;

/**
 * @author enjoyyin
 * @date 2022-03-16
 * @since 0.5.0
 */
public class ProjectToRelConversionService extends AbstractConversionService
        implements DSSToRelConversionService {
    @Override
    public DSSToRelConversionOperation getDSSToRelConversionOperation() {
        return getOrCreate(ProjectToRelConversionOperation::new, ProjectToRelConversionOperation.class);
    }
}
