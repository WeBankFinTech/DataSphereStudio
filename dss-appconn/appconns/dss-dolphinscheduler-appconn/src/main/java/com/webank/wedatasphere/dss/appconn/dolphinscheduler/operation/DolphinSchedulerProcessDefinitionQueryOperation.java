package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerProjectResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerGetRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpGet;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinAppConnUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.SchedulisExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.CommonRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.URIBuilder;

public class DolphinSchedulerProcessDefinitionQueryOperation implements RefQueryOperation {

    private AppDesc appDesc;

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private SSORequestOperation<DolphinSchedulerHttpGet, CloseableHttpResponse> getOperation;

    private String queryProcessDefinitionByIdUrl;

    public DolphinSchedulerProcessDefinitionQueryOperation(AppDesc appDesc) {
        this.appDesc = appDesc;
        String baseUrl = this.appDesc.getAppInstances().get(0).getBaseUrl();
        this.getOperation = new DolphinSchedulerGetRequestOperation(baseUrl);
        this.queryProcessDefinitionByIdUrl = baseUrl.endsWith("/") ?
            baseUrl + "projects/${projectName}/process/select-by-id" :
            baseUrl + "/projects/${projectName}/process/select-by-id";
    }

    private String queryProcessDefinitionReleaseStateById(String projectName, Long processId, String userName)
        throws ExternalOperationFailedException {
        String queryUrl = StringUtils.replace(this.queryProcessDefinitionByIdUrl, "${projectName}", projectName);

        CloseableHttpResponse httpResponse = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(queryUrl);
            uriBuilder.addParameter("processId", String.valueOf(processId));
            DolphinSchedulerHttpGet httpGet = new DolphinSchedulerHttpGet(uriBuilder.build(), userName);

            httpResponse = this.getOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpGet);
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), "utf-8");

            if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()
                && DolphinAppConnUtils.getCodeFromEntity(entString) == 0) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(entString);
                JsonNode dataNode = jsonNode.get("data");
                return dataNode.get("releaseState").asText();
            } else {
                throw new ExternalOperationFailedException(90013, "从Dolphin Scheduler获取工作流定义状态失败, 原因:" + entString);
            }
        } catch (final Throwable t) {
            SchedulisExceptionUtils.dealErrorException(90013,
                "failed to get process definition release state from Dolphin Scheduler", t,
                ExternalOperationFailedException.class);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }
        return null;
    }

    @Override
    public ResponseRef query(RequestRef ref) throws ExternalOperationFailedException {
        CommonRequestRef requestRef = (CommonRequestRef)ref;
        String dolphinProjectName = ProjectUtils.generateDolphinProjectName(requestRef.getWorkspaceName(),
            requestRef.getProjectName());
        String releaseState = queryProcessDefinitionReleaseStateById(dolphinProjectName,
            (Long)requestRef.getParameter("processId"), (String)requestRef.getParameter("username"));
        DolphinSchedulerProjectResponseRef responseRef = new DolphinSchedulerProjectResponseRef(releaseState);
        return responseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {

    }
}
