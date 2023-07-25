package com.webank.wedatasphere.dss.framework.project.utils;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyStructureAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerAppConn;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.utils.StructureOperationUtils;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.function.*;

/**
 * @author enjoyyin
 * @date 2022-03-14
 * @since 0.5.0
 */
public class ProjectOperationUtils {

    public static  <K extends StructureRequestRef, V extends ResponseRef> void tryProjectOperation(BiPredicate<AppConn, AppInstance> isTryOperation, Workspace workspace,
                                                                                            Function<ProjectService, StructureOperation> getProjectOperation,
                                                                                            Consumer<DSSProjectContentRequestRef> dssProjectContentRequestRefConsumer,
                                                                                            BiConsumer<AppInstance, RefProjectContentRequestRef> refProjectContentRequestRefConsumer,
                                                                                            BiFunction<StructureOperation, K, V> responseRefConsumer,
                                                                                            BiConsumer<ImmutablePair<AppConn, AppInstance>, V> dealResponseRefConsumer,
                                                                                            String errorMsg) {
        // 排序，将 SchedulerAppConn 放在最前面进行操作，因为调度系统的操作最危险，如果有问题就第一个抛出来
        AppConnManager.getAppConnManager().listAppConns().stream().sorted((appConn1, appConn2) -> {
            if(appConn1 instanceof SchedulerAppConn) {
                return -1;
            } else if(appConn2 instanceof SchedulerAppConn){
                return 1;
            } else {
                return 0;
            }
        }).filter(appConn -> appConn instanceof OnlyStructureAppConn).forEach(appConn -> {
            StructureIntegrationStandard structureStandard = ((OnlyStructureAppConn) appConn).getOrCreateStructureStandard();
            appConn.getAppDesc().getAppInstances().forEach(appInstance -> {
                if(isTryOperation == null || isTryOperation.test(appConn, appInstance)) {
                    V responseRef = StructureOperationUtils.tryProjectOperation(() -> structureStandard.getProjectService(appInstance),
                            getProjectOperation,
                            dssProjectContentRequestRefConsumer,
                            refProjectContentRequestRef -> refProjectContentRequestRefConsumer.accept(appInstance, refProjectContentRequestRef),
                            (structureOperation, structureRequestRef) -> {
                                structureRequestRef.setDSSLabels(appInstance.getLabels()).setWorkspace(workspace);
                                return responseRefConsumer.apply(structureOperation, (K) structureRequestRef);
                            }, appConn.getAppDesc().getAppName() + " try to " + errorMsg);
                    if(dealResponseRefConsumer != null) {
                        dealResponseRefConsumer.accept(new ImmutablePair<>(appConn, appInstance), responseRef);
                    }
                }
            });
        });
    }

}
