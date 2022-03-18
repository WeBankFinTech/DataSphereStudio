package com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref;

import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;

/**
 * @author enjoyyin
 * @date 2022-03-14
 * @since 0.5.0
 */
public interface RefOrchestrationContentRequestRef<R extends RefOrchestrationContentRequestRef<R>>
        extends RefProjectContentRequestRef<R>, StructureRequestRef<R> {

    default Long getRefOrchestrationId() {
        return (Long) getParameter("refOrchestrationId");
    }

    default R setRefOrchestrationId(Long refOrchestrationId) {
        setParameter("refOrchestrationId", refOrchestrationId);
        return (R) this;
    }

    default String getOrchestrationName() {
        return (String) getParameter("orchestrationName");
    }

    default R setOrchestrationName(String orchestrationName) {
        setParameter("orchestrationName", orchestrationName);
        return (R) this;
    }

    class RefOrchestrationContentRequestRefImpl extends StructureRequestRefImpl<RefOrchestrationContentRequestRefImpl>
            implements RefOrchestrationContentRequestRef<RefOrchestrationContentRequestRefImpl> {}

}
