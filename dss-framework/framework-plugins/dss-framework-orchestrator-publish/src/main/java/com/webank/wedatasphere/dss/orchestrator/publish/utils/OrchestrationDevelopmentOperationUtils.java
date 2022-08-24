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
 * 编排相关operation执行工具
 * @author enjoyyin
 * @date 2022-03-12
 * @since 0.5.0
 */
public class OrchestrationDevelopmentOperationUtils {
    /**
     * 执行编排相关的operation
     * @param dssOrchestratorInfo 编排信息
     * @param dssOrchestrator 编排系统资源，作为操作的执行者。
     * @param userName 用户名
     * @param workspace 工作空间
     * @param dssLabels label，比如环境label
     * @param getDevelopmentService DevelopmentService提供器，比如可以提供一个WorkflowExportService用于最终提供工作流导出服务
     * @param getDevelopmentOperation  Operation提供器，比如可以提供一个WorkflowRefExportOperation用于工作流导出操作
     * @param contextRequestRefConsumer requestRef的context处理器，用以对DSSContextRequestRef的requestRef做处理
     * @param projectRefRequestRefConsumer requestRef的project处理器，用以对ProjectRefRequestRef的requestRef做处理
     * @param responseRefConsumer 上游提供的operation执行器，用以operation执行以及对responseRef的处理
     * @param operationName 执行的操作名
     * @return 得到的操作结果ResponseRef
     * @param <K> 操作入参RequestRef具体类型
     * @param <V> 操作结果ResponseRef的具体类型
     */
    public static <K extends DevelopmentRequestRef, V extends ResponseRef> V tryOrchestrationOperation(DSSOrchestratorInfo dssOrchestratorInfo,
                                                                                       DSSOrchestrator dssOrchestrator,
                                                                                       String userName,
                                                                                       Workspace workspace,
                                                                                       List<DSSLabel> dssLabels,
                                                                                       BiFunction<DevelopmentIntegrationStandard, AppInstance, DevelopmentService> getDevelopmentService,
                                                                                       Function<DevelopmentService, DevelopmentOperation> getDevelopmentOperation,
                                                                                       Consumer<DSSContextRequestRef> contextRequestRefConsumer,
                                                                                       Consumer<ProjectRefRequestRef> projectRefRequestRefConsumer,
                                                                                       BiFunction<DevelopmentOperation, K, V> responseRefConsumer,
                                                                                       String operationName) throws DSSErrorException {

        //第一步，获取编排实现的APPInstance（比如工作流服务）和对应的DevelopmentIntegrationStandard
        ImmutablePair<AppInstance, DevelopmentIntegrationStandard> standMap =
                OrchestratorLoaderUtils.getOrchestratorDevelopmentStandard(dssOrchestrator, dssLabels);
        //第二步，执行这个APPInstance上实现的DevelopmentIntegrationStandard。
        //至于要执行DevelopmentIntegrationStandard里的什么operation，就需要根据外部传入的getDevelopmentService和getDevelopmentOperation决定
        return DevelopmentOperationUtils.tryDevelopmentRequestRefOperation(
                //目的是让下游得到DevelopmentService。
                // 根据不同的AppInstance，getDevelopmentService可以返回不同是实现类，比如是一个WorkflowExportService。
                // 因此下游就会得到一个具体的DevelopmentService，比如一个WorkflowExportService，这样下游便可以执行工作流导出了。
                () -> getDevelopmentService.apply(standMap.getValue(), standMap.getKey()),
                //告诉下游如何从DevelopmentService得到operation，比如从从RefExportOperation::getRefExportOperation,得到WorkflowRefExportOperation
                getDevelopmentOperation,
                //给下游一个context处理器，对DSSContextRequestRef类型的requestRef做处理
                contextRequestRefConsumer,
                //给下游一个project处理器，对ProjectRefRequestRef类型的requestRef做处理
                projectRefRequestRefConsumer,
                //给下游一个operation的执行器，执行operation（最终也是通过上游提供的responseRefConsumer去执行operation）
                (developmentOperation, developmentRequestRef) -> {
                    developmentRequestRef.setWorkspace(workspace).setUserName(userName).setDSSLabels(dssLabels).setType(dssOrchestratorInfo.getType());
                    return responseRefConsumer.apply(developmentOperation, (K) developmentRequestRef);
                },
                "Orchestrator type " + dssOrchestratorInfo.getType() + " with binding AppConn "
                        + dssOrchestratorInfo.getAppConnName()  + " try to " + operationName);
    }

}
