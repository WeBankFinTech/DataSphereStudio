package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerDataResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerPageInfoResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerHttpUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationSearchOperation;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.OrchestrationResponseRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.RefOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class DolphinSchedulerWorkflowSearchOperation
        extends AbstractStructureOperation<RefOrchestrationContentRequestRef.RefOrchestrationContentRequestRefImpl, OrchestrationResponseRef>
        implements OrchestrationSearchOperation<RefOrchestrationContentRequestRef.RefOrchestrationContentRequestRefImpl> {

    private String getProcessDefinitionByIdUrl;

    @Override
    protected String getAppConnName() {
        return DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        String baseUrl = DolphinSchedulerHttpUtils.getDolphinSchedulerBaseUrl(getBaseUrl());
        this.getProcessDefinitionByIdUrl = mergeUrl(baseUrl, "projects/${projectName}/process/list");
    }

    @Override
    public OrchestrationResponseRef searchOrchestration(RefOrchestrationContentRequestRef.RefOrchestrationContentRequestRefImpl ref) throws ExternalOperationFailedException {
        String dolphinProjectName =
                ProjectUtils.generateDolphinProjectName(ref.getWorkspace().getWorkspaceName(), ref.getProjectName());
        String getUrl = StringUtils.replace(this.getProcessDefinitionByIdUrl, "${projectName}", dolphinProjectName);
        DolphinSchedulerDataResponseRef responseRef = DolphinSchedulerHttpUtils.getHttpGetResult(ssoRequestOperation, getUrl, ref.getUserName());
        List<Map<String, Object>> dataList = responseRef.getData();
        return dataList.stream().filter(workflow -> ref.getOrchestrationName().equals(workflow.get("name")))
                .map(workflow -> DolphinSchedulerHttpUtils.parseToLong(workflow.get("id"))).map(id -> OrchestrationResponseRef.newExternalBuilder().setRefOrchestrationId(id).success())
                .findAny().orElse(OrchestrationResponseRef.newExternalBuilder().success());
    }

//    private String queryProcessDefinitionReleaseStateById(String projectName, Long processId, String userName)
//        throws ExternalOperationFailedException {
//        String queryUrl = StringUtils.replace(this.verifyProcessDefinitionByIdUrl, "${projectName}", projectName);
//
//        CloseableHttpResponse httpResponse = null;
//        String entString = null;
//        int httpStatusCode = 0;
//        try {
//            URIBuilder uriBuilder = new URIBuilder(queryUrl);
//            uriBuilder.addParameter("processId", String.valueOf(processId));
//            DolphinSchedulerHttpGet httpGet = new DolphinSchedulerHttpGet(uriBuilder.build(), userName);
//
//            httpResponse = this.getOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpGet);
//
//            HttpEntity ent = httpResponse.getEntity();
//            entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
//            httpStatusCode = httpResponse.getStatusLine().getStatusCode();
//        } catch (final Exception e) {
//            SchedulisExceptionUtils.dealErrorException(90021, "获取工作流调度状态失败", e, ExternalOperationFailedException.class);
//        } finally {
//            IOUtils.closeQuietly(httpResponse);
//        }
//
//        try {
//            if (HttpStatus.SC_OK == httpStatusCode && DolphinAppConnUtils.getCodeFromEntity(entString) == 0) {
//                ObjectMapper mapper = new ObjectMapper();
//                JsonNode jsonNode = mapper.readTree(entString);
//                JsonNode dataNode = jsonNode.get("data");
//                return dataNode.get("releaseState").asText();
//            }
//        } catch (IOException e) {
//            throw new ExternalOperationFailedException(90022, "工作流调度状态解析失败", e);
//        }
//        logger.warn("Dolphin Scheduler上不存在该工作流定义:{}", entString);
//        return null;
//    }

}
