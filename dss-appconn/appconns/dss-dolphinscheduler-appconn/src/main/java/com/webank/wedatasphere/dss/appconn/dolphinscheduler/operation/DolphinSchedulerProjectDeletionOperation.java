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

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerProjectResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerProjectService;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerGetRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpGet;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinAppConnUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.SchedulisExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * The type Dolphin scheduler project deletion operation.
 *
 * @author yuxin.yuan
 * @date 2021/06/04
 */
public class DolphinSchedulerProjectDeletionOperation implements ProjectDeletionOperation {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerProjectDeletionOperation.class);

    private DolphinSchedulerProjectService dolphinSchedulerProjectService;

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private SSORequestOperation<DolphinSchedulerHttpGet, CloseableHttpResponse> getOperation;

    private String deleteProcessDefinitionByIdUrl;

    private String deleteProjectByIdUrl;

    public DolphinSchedulerProjectDeletionOperation(DolphinSchedulerProjectService dolphinSchedulerProjectService) {
        this.dolphinSchedulerProjectService = dolphinSchedulerProjectService;

        String baseUrl = this.dolphinSchedulerProjectService.getAppInstance().getBaseUrl();
        this.getOperation = new DolphinSchedulerGetRequestOperation(baseUrl);
        this.deleteProcessDefinitionByIdUrl = baseUrl.endsWith("/") ? baseUrl + "projects/${projectName}/process/delete"
            : baseUrl + "/projects/${projectName}/process/delete";
        this.deleteProjectByIdUrl = baseUrl.endsWith("/") ? baseUrl + "projects/delete" : baseUrl + "/projects/delete";
    }

    @Override
    public void setStructureService(StructureService service) {

    }

    @Override
    public ProjectResponseRef deleteProject(ProjectRequestRef projectRef) throws ExternalOperationFailedException {
        // 删除工作流定义
        if ("Orchestrator".equalsIgnoreCase(projectRef.getType())) {
            deleteDolphinSchedulerProcessDefinition(projectRef);
        } else if ("Project".equalsIgnoreCase(projectRef.getType())) {
            deleteDolphinSchedulerProject(projectRef);
        }

        return new DolphinSchedulerProjectResponseRef();
    }

    private void deleteDolphinSchedulerProcessDefinition(ProjectRequestRef projectRef)
        throws ExternalOperationFailedException {
        String dolphinProjectName =
            ProjectUtils.generateDolphinProjectName(projectRef.getWorkspaceName(), projectRef.getName());
        String deleteUrl =
            StringUtils.replace(this.deleteProcessDefinitionByIdUrl, "${projectName}", dolphinProjectName);

        CloseableHttpResponse httpResponse = null;
        String entString = null;
        int httpStatusCode = 0;
        try {
            URIBuilder uriBuilder = new URIBuilder(deleteUrl);
            uriBuilder.addParameter("processDefinitionId", String.valueOf(projectRef.getId()));
            DolphinSchedulerHttpGet httpGet = new DolphinSchedulerHttpGet(uriBuilder.build(), projectRef.getCreateBy());

            httpResponse = this.getOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpGet);

            HttpEntity ent = httpResponse.getEntity();
            entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
            httpStatusCode = httpResponse.getStatusLine().getStatusCode();
        } catch (final Exception e) {
            SchedulisExceptionUtils.dealErrorException(90031, "删除调度中心工作流失败", e, ExternalOperationFailedException.class);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }

        try {
            if (HttpStatus.SC_OK == httpStatusCode) {
                int resultCode = DolphinAppConnUtils.getCodeFromEntity(entString);
                if (resultCode == 0 || resultCode == 50003) { // 删除成功/工作流定义不存在
                    logger.info("Dolphin Scheduler删除工作流定义 {} 成功", projectRef.getId());
                } else if (resultCode == 50021) {
                    throw new ExternalOperationFailedException(90032, "该工作流已上线，请先在调度中心下线");
                } else {
                    throw new ExternalOperationFailedException(90031, "删除调度中心工作流失败, 原因:" + entString);
                }
            }
        } catch (IOException e) {
            throw new ExternalOperationFailedException(90031, "删除调度中心工作流失败", e);
        }
    }

    private void deleteDolphinSchedulerProject(ProjectRequestRef projectRef) throws ExternalOperationFailedException {
        CloseableHttpResponse httpResponse = null;
        String entString = null;
        int httpStatusCode = 0;
        try {
            URIBuilder uriBuilder = new URIBuilder(this.deleteProjectByIdUrl);
            uriBuilder.addParameter("projectId", String.valueOf(projectRef.getId()));
            DolphinSchedulerHttpGet httpGet = new DolphinSchedulerHttpGet(uriBuilder.build(), projectRef.getCreateBy());

            httpResponse = this.getOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpGet);

            HttpEntity ent = httpResponse.getEntity();
            entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
            httpStatusCode = httpResponse.getStatusLine().getStatusCode();
        } catch (final Exception e) {
            SchedulisExceptionUtils.dealErrorException(90033, "删除调度中心项目失败", e, ExternalOperationFailedException.class);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }

        try {
            if (HttpStatus.SC_OK == httpStatusCode) {
                int resultCode = DolphinAppConnUtils.getCodeFromEntity(entString);
                if (resultCode == 0 || resultCode == 10018) { // 删除成功/项目不存在
                    logger.info("Dolphin Scheduler删除项目 {} 成功",
                        ProjectUtils.generateDolphinProjectName(projectRef.getWorkspaceName(), projectRef.getName()));
                } else if (resultCode == 10137) {
                    throw new ExternalOperationFailedException(90034, "该工程项下存在工作流，请先删除对应工作流");
                } else {
                    throw new ExternalOperationFailedException(90033, "删除调度中心项目失败, 原因:" + entString);
                }
            }
        } catch (IOException e) {
            throw new ExternalOperationFailedException(90033, "删除调度中心项目失败", e);
        }
    }
}
