package com.webank.wedatasphere.dss.orchestrator.publish.utils;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.loader.utils.OrchestratorLoaderUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.DevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.DSSContextRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.DevelopmentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.ProjectRefRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.utils.DevelopmentOperationUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import org.apache.linkis.protocol.util.ImmutablePair;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author enjoyyin
 * @date 2022-03-12
 * @since 0.5.0
 */
public class OrchestrationDevelopmentOperationUtils {

    public static <K extends DevelopmentRequestRef, V extends ResponseRef> V tryOrchestrationOperation(DSSOrchestratorInfo dssOrchestratorInfo,
                                                                                       DSSOrchestrator dssOrchestrator,
                                                                                       String userName,
                                                                                       Workspace workspace,
                                                                                       List<DSSLabel> dssLabels,
                                                                                       Function<DevelopmentService, DevelopmentOperation> getDevelopmentOperation,
                                                                                       Consumer<DSSContextRequestRef> contextRequestRefConsumer,
                                                                                       Consumer<ProjectRefRequestRef> projectRefRequestRefConsumer,
                                                                                       BiFunction<DevelopmentOperation, K, V> responseRefConsumer,
                                                                                       String operationName) throws DSSErrorException {
        ImmutablePair<AppInstance, DevelopmentIntegrationStandard> standMap =
                OrchestratorLoaderUtils.getOrchestratorDevelopmentStandard(dssOrchestrator, dssLabels);
        return DevelopmentOperationUtils.tryDevelopmentRequestRefOperation(() -> standMap.getValue().getRefCRUDService(standMap.getKey()),
                getDevelopmentOperation, contextRequestRefConsumer, projectRefRequestRefConsumer,
                (developmentOperation, developmentRequestRef) -> {
                    developmentRequestRef.setWorkspace(workspace).setUserName(userName).setDSSLabels(dssLabels).setType(dssOrchestratorInfo.getType());
                    return responseRefConsumer.apply(developmentOperation, (K) developmentRequestRef);
                },
                "Orchestrator type " + dssOrchestratorInfo.getType() + " with binding AppConn "
                        + dssOrchestratorInfo.getAppConnName()  + " try to " + operationName);
    }

}
