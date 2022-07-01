package com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;

/**
 * @author enjoyyin
 * @date 2022-03-14
 * @since 0.5.0
 */
public interface OrchestrationUpdateRequestRef<R extends OrchestrationUpdateRequestRef<R>>
        extends DSSOrchestrationContentRequestRef<R>, RefOrchestrationContentRequestRef<R> {

    @Override
    default String getOrchestrationName() {
        return getDSSOrchestration().getName();
    }

    @Override
    default R setOrchestrationName(String orchestrationName) {
        if(getDSSOrchestration() instanceof DSSOrchestratorInfo) {
            ((DSSOrchestratorInfo) getDSSOrchestration()).setName(orchestrationName);
        }
        return (R) this;
    }

    class OrchestrationUpdateRequestRefImpl extends StructureRequestRefImpl<OrchestrationUpdateRequestRefImpl>
            implements OrchestrationUpdateRequestRef<OrchestrationUpdateRequestRefImpl> {}

}
