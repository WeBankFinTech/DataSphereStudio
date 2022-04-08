package com.webank.wedatasphere.dss.appconn.scheduler.utils;

import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationService;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.DSSOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.RefOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.webank.wedatasphere.dss.standard.app.structure.utils.StructureOperationUtils.tryStructureOperation;

/**
 * @author enjoyyin
 * @date 2022-03-14
 * @since 1.1.0
 */
public class OrchestrationOperationUtils {

    public static <K extends StructureRequestRef, V extends ResponseRef> V tryOrchestrationOperation(Supplier<OrchestrationService> getOrchestrationService,
                                                                                               Function<OrchestrationService, StructureOperation> getOrchestrationOperation,
                                                                                               Consumer<DSSOrchestrationContentRequestRef> dssOrchestrationContentRequestRefConsumer,
                                                                                               Consumer<RefOrchestrationContentRequestRef> refOrchestrationContentRequestRefConsumer,
                                                                                               BiFunction<StructureOperation, K, V> responseRefConsumer,
                                                                                               String errorMsg) {
        return tryStructureOperation(getOrchestrationService, getOrchestrationOperation, structureRequestRef -> {
            if(dssOrchestrationContentRequestRefConsumer != null && structureRequestRef instanceof DSSOrchestrationContentRequestRef) {
                dssOrchestrationContentRequestRefConsumer.accept((DSSOrchestrationContentRequestRef) structureRequestRef);
            }
            if(refOrchestrationContentRequestRefConsumer != null && structureRequestRef instanceof RefOrchestrationContentRequestRef) {
                refOrchestrationContentRequestRefConsumer.accept((RefOrchestrationContentRequestRef) structureRequestRef);
            }
        }, responseRefConsumer, errorMsg);
    }
    
}
