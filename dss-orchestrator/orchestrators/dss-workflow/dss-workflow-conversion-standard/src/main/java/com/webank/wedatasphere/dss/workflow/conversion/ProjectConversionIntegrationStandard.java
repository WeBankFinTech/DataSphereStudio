package com.webank.wedatasphere.dss.workflow.conversion;

import com.webank.wedatasphere.dss.orchestrator.converter.standard.AbstractConversionIntegrationStandard;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.DSSToRelConversionService;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.service.RelToOrchestratorConversionService;
import com.webank.wedatasphere.dss.workflow.conversion.service.ProjectToRelConversionService;

/**
 * @author enjoyyin
 * @date 2022-03-16
 * @since 0.5.0
 */
public class ProjectConversionIntegrationStandard extends AbstractConversionIntegrationStandard {

    @Override
    protected DSSToRelConversionService createDSSToRelConversionService() {
        return new ProjectToRelConversionService();
    }

    @Override
    protected RelToOrchestratorConversionService createRelToDSSConversionService() {
        return null;
    }
}
