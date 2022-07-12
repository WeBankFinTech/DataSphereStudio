package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerWorkflow;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerDataResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerHttpUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationUpdateOperation;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.OrchestrationUpdateRequestRef;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.utils.JsonUtils;

import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-03-17
 * @since 0.5.0
 */
public class DolphinSchedulerWorkflowUpdateOperation
        extends AbstractStructureOperation<OrchestrationUpdateRequestRef.OrchestrationUpdateRequestRefImpl, ResponseRef>
implements OrchestrationUpdateOperation<OrchestrationUpdateRequestRef.OrchestrationUpdateRequestRefImpl> {

    private String updateProcessDefinitionByIdUrl;
    private String selectProcessDefinitionByIdUrl;

    @Override
    public void init() {
        super.init();
        String baseUrl = DolphinSchedulerHttpUtils.getDolphinSchedulerBaseUrl(getBaseUrl());
        this.updateProcessDefinitionByIdUrl = mergeUrl(baseUrl, "projects/${projectName}/process/update");
        this.selectProcessDefinitionByIdUrl = mergeUrl(baseUrl, "projects/${projectName}/process/select-by-id");
    }

    @Override
    public ResponseRef updateOrchestration(OrchestrationUpdateRequestRef.OrchestrationUpdateRequestRefImpl orchestrationRef) throws ExternalOperationFailedException {
        String dolphinProjectName =
                ProjectUtils.generateDolphinProjectName(orchestrationRef.getWorkspace().getWorkspaceName(), orchestrationRef.getProjectName());
        logger.info("begin to update workflow in DolphinScheduler, project name is {}, workflow name is {}, creator is {}.",
                dolphinProjectName, orchestrationRef.getDSSOrchestration().getName(), orchestrationRef.getUserName());
        String updateUrl =
                StringUtils.replace(this.updateProcessDefinitionByIdUrl, "${projectName}", dolphinProjectName);
        String processDefinitionJson, locations, connects;
        if(orchestrationRef.getDSSOrchestration() instanceof DolphinSchedulerWorkflow) {
            DolphinSchedulerWorkflow workflow = (DolphinSchedulerWorkflow) orchestrationRef.getDSSOrchestration();
            try {
                processDefinitionJson = JsonUtils.jackson().writeValueAsString(workflow.getProcessDefinitionJson());
                locations = JsonUtils.jackson().writeValueAsString(workflow.getLocations());
                connects = JsonUtils.jackson().writeValueAsString(workflow.getConnects());
            } catch (JsonProcessingException e) {
                throw new ExternalOperationFailedException(90321, "parse workflow object to DolphinScheduler workflow string failed.");
            }
        } else {
            String selectUrl = StringUtils.replace(selectProcessDefinitionByIdUrl, "${projectName}", dolphinProjectName);
            DolphinSchedulerDataResponseRef responseRef = DolphinSchedulerHttpUtils.getHttpGetResult(ssoRequestOperation, selectUrl + "?processId=" +
                    orchestrationRef.getRefOrchestrationId(), orchestrationRef.getUserName());
            Map<String, Object> workflow = responseRef.getData();
            processDefinitionJson = (String) workflow.get("processDefinitionJson");
            locations = (String) workflow.get("locations");
            connects = (String) workflow.get("connects");
        }
        Map<String, Object> formData = MapUtils.newCommonMapBuilder().put("name", orchestrationRef.getDSSOrchestration().getName())
                .put("description", orchestrationRef.getDSSOrchestration().getDescription())
                .put("id", orchestrationRef.getRefOrchestrationId())
                .put("processDefinitionJson", processDefinitionJson)
                .put("locations", locations).put("connects", connects).build();
        DolphinSchedulerHttpUtils.getHttpPostResult(ssoRequestOperation, updateUrl, orchestrationRef.getUserName(), formData);
        return ResponseRef.newExternalBuilder().success();
    }

    @Override
    protected String getAppConnName() {
        return DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME;
    }
}
