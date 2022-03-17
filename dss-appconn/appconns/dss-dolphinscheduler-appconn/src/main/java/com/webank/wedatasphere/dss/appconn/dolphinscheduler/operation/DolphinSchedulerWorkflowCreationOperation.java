package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerHttpUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationCreationOperation;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationService;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.DSSOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.OrchestrationResponseRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.RefOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-03-17
 * @since 0.5.0
 */
public class DolphinSchedulerWorkflowCreationOperation
        extends AbstractStructureOperation<DSSOrchestrationContentRequestRef.DSSOrchestrationContentRequestRefImpl, OrchestrationResponseRef>
        implements OrchestrationCreationOperation<DSSOrchestrationContentRequestRef.DSSOrchestrationContentRequestRefImpl> {

    private String createProcessDefinitionByIdUrl;

    @Override
    public void init() {
        super.init();
        this.createProcessDefinitionByIdUrl = mergeBaseUrl("projects/${projectName}/process/save");
    }

    @Override
    public OrchestrationResponseRef createOrchestration(DSSOrchestrationContentRequestRef.DSSOrchestrationContentRequestRefImpl orchestrationRef) throws ExternalOperationFailedException {
        String dolphinProjectName =
                ProjectUtils.generateDolphinProjectName(orchestrationRef.getWorkspace().getWorkspaceName(), orchestrationRef.getProjectName());
        logger.info("begin to create workflow in DolphinScheduler, project name is {}, workflow name is {}, creator is {}.",
                dolphinProjectName, orchestrationRef.getDSSOrchestration().getName(), orchestrationRef.getUserName());
        String createUrl =
                StringUtils.replace(this.createProcessDefinitionByIdUrl, "${projectName}", dolphinProjectName);
        Map<String, Object> formData = MapUtils.newCommonMapBuilder().put("name", orchestrationRef.getDSSOrchestration().getName())
                .put("description", orchestrationRef.getDSSOrchestration().getDescription())
                .put("processDefinitionJson", "{}").put("locations", "").put("connects", "[]").build();
        DolphinSchedulerHttpUtils.getHttpPostResult(ssoRequestOperation, createUrl, orchestrationRef.getUserName(), formData);
        // 获取id
        RefOrchestrationContentRequestRef.RefOrchestrationContentRequestRefImpl ref = new RefOrchestrationContentRequestRef.RefOrchestrationContentRequestRefImpl()
                .setRefProjectId(orchestrationRef.getRefProjectId()).setOrchestrationName(orchestrationRef.getDSSOrchestration().getName())
                .setProjectName(orchestrationRef.getProjectName()).setWorkspace(orchestrationRef.getWorkspace())
                .setUserName(orchestrationRef.getUserName());
        return ((OrchestrationService) service).getOrchestrationSearchOperation().searchOrchestration(ref);
    }

    @Override
    protected String getAppConnName() {
        return DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME;
    }
}
