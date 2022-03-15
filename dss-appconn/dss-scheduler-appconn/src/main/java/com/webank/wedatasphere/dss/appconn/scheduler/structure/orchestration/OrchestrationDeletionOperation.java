package com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration;

import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.RefOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * @author enjoyyin
 * @date 2022-03-14
 * @since 0.5.0
 */
public interface OrchestrationDeletionOperation<R extends RefOrchestrationContentRequestRef<R>>
        extends StructureOperation<R, ResponseRef> {

    /**
     * delete the related refOrchestration in third-party AppConn by refOrchestrationId
     * which returned by OrchestrationCreationOperation.
     * refOrchestrationId must not be null, please use it to delete the refOrchestration.
     * @param requestRef refOrchestration info, refOrchestrationId must not be null
     * @return the result of deletion, just success or failure.
     * @throws ExternalOperationFailedException
     */
    ResponseRef deleteOrchestration(R requestRef);

}
