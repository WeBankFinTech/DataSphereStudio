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
        String baseUrl = DolphinSchedulerHttpUtils.getDolphinSchedulerBaseUrl(getBaseUrl());
        this.createProcessDefinitionByIdUrl = mergeUrl(baseUrl, "projects/${projectName}/process/save");
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
                .put("processDefinitionJson", "{\"globalParams\":[],\"tasks\":[{\"type\":\"SHELL\",\"id\":\"DSS_INIT_EMPTY_NODE\",\"name\":\"init_empty_node\",\"params\":{\"resourceList\":[],\"localParams\":[],\"rawScript\":\"echo \\\"This node is only used for DSS to create the workflow, when a publishment is called by DSS, this workflow will be updated by DSS.\\\"\"},\"description\":\"\",\"timeout\":{\"strategy\":\"\",\"interval\":null,\"enable\":false},\"runFlag\":\"NORMAL\",\"conditionResult\":{\"successNode\":[\"\"],\"failedNode\":[\"\"]},\"dependence\":{},\"maxRetryTimes\":\"0\",\"retryInterval\":\"1\",\"taskInstancePriority\":\"MEDIUM\",\"workerGroup\":\"default\",\"preTasks\":[]}],\"tenantId\":1,\"timeout\":0}")
                .put("locations", "{\"DSS_INIT_EMPTY_NODE\":{\"name\":\"init_empty_node\",\"targetarr\":\"\",\"nodenumber\":\"0\",\"x\":236,\"y\":60}}")
                .put("connects", "[]").build();
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
