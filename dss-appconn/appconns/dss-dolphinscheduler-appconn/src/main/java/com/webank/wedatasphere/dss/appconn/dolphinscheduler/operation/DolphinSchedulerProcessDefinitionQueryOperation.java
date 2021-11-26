package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerProjectResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerGetRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpGet;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinAppConnUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.SchedulisExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CommonRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * The type Dolphin scheduler process definition query operation.
 *
 * @author yuxin.yuan
 * @date 2021/10/29
 */
public class DolphinSchedulerProcessDefinitionQueryOperation implements RefQueryOperation {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerProcessDefinitionQueryOperation.class);

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private SSORequestOperation<DolphinSchedulerHttpGet, CloseableHttpResponse> getOperation;

    private String queryProcessDefinitionByIdUrl;

    public DolphinSchedulerProcessDefinitionQueryOperation(String baseUrl) {
        this.queryProcessDefinitionByIdUrl =
            baseUrl.endsWith("/") ? baseUrl + "projects/${projectName}/process/select-by-id"
                : baseUrl + "/projects/${projectName}/process/select-by-id";

        this.getOperation = new DolphinSchedulerGetRequestOperation(baseUrl);
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {

    }

    @Override
    public ResponseRef query(RequestRef ref) throws ExternalOperationFailedException {
        CommonRequestRef requestRef = (CommonRequestRef)ref;
        String dolphinProjectName =
            ProjectUtils.generateDolphinProjectName(requestRef.getWorkspaceName(), requestRef.getProjectName());
        String releaseState = queryProcessDefinitionReleaseStateById(dolphinProjectName,
            (Long)requestRef.getParameter("processId"), (String)requestRef.getParameter("username"));
        return new DolphinSchedulerProjectResponseRef(releaseState);
    }

    private String queryProcessDefinitionReleaseStateById(String projectName, Long processId, String userName)
        throws ExternalOperationFailedException {
        String queryUrl = StringUtils.replace(this.queryProcessDefinitionByIdUrl, "${projectName}", projectName);

        CloseableHttpResponse httpResponse = null;
        String entString = null;
        int httpStatusCode = 0;
        try {
            URIBuilder uriBuilder = new URIBuilder(queryUrl);
            uriBuilder.addParameter("processId", String.valueOf(processId));
            DolphinSchedulerHttpGet httpGet = new DolphinSchedulerHttpGet(uriBuilder.build(), userName);

            httpResponse = this.getOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpGet);

            HttpEntity ent = httpResponse.getEntity();
            entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
            httpStatusCode = httpResponse.getStatusLine().getStatusCode();
        } catch (final Exception e) {
            SchedulisExceptionUtils.dealErrorException(90021, "获取工作流调度状态失败", e, ExternalOperationFailedException.class);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }

        try {
            if (HttpStatus.SC_OK == httpStatusCode && DolphinAppConnUtils.getCodeFromEntity(entString) == 0) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(entString);
                JsonNode dataNode = jsonNode.get("data");
                return dataNode.get("releaseState").asText();
            }
        } catch (IOException e) {
            throw new ExternalOperationFailedException(90022, "工作流调度状态解析失败", e);
        }
        logger.warn("Dolphin Scheduler上不存在该工作流定义:{}", entString);
        return null;
    }

}
