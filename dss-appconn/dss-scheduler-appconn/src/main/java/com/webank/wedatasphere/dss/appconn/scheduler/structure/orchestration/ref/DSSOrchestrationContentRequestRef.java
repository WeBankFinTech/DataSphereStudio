package com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestration;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;

/**
 * @author enjoyyin
 * @date 2022-03-14
 * @since 0.5.0
 */
public interface DSSOrchestrationContentRequestRef<R extends DSSOrchestrationContentRequestRef<R>>
        extends RefProjectContentRequestRef<R>, StructureRequestRef<R> {

    default DSSOrchestration getDSSOrchestration() {
        return (DSSOrchestration) getParameter("dssOrchestration");
    }

    default R setDSSOrchestration(DSSOrchestration dssOrchestration) {
        setParameter("dssOrchestration", dssOrchestration);
        return (R) this;
    }

    class DSSOrchestrationContentRequestRefImpl extends StructureRequestRefImpl<DSSOrchestrationContentRequestRefImpl>
            implements DSSOrchestrationContentRequestRef<DSSOrchestrationContentRequestRefImpl> {}

}
