package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.constant.Constant;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerProjectResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerProjectService;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpPost;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerPostRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinAppConnUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * The type Dolphin scheduler project update operation.
 *
 * @author yuxin.yuan
 * @date 2021/06/23
 */
public class DolphinSchedulerProjectUpdateOperation implements ProjectUpdateOperation, DolphinSchedulerConf {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerProjectUpdateOperation.class);

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private DolphinSchedulerProjectService dolphinSchedulerProjectService;

    private String baseUrl;

    private SSORequestOperation<DolphinSchedulerHttpPost, CloseableHttpResponse> postOperation;

    private String projectUpdateUrl;

    public DolphinSchedulerProjectUpdateOperation(DolphinSchedulerProjectService dolphinSchedulerProjectService) {
        this.dolphinSchedulerProjectService = dolphinSchedulerProjectService;
        init();
    }

    private void init() {
        this.baseUrl = this.dolphinSchedulerProjectService.getAppInstance().getBaseUrl();
        this.postOperation = new DolphinSchedulerPostRequestOperation(baseUrl);
        this.projectUpdateUrl = baseUrl.endsWith("/") ? baseUrl + "projects/update" : baseUrl + "/projects/update";
    }

    @Override
    public ProjectResponseRef updateProject(ProjectRequestRef requestRef) throws ExternalOperationFailedException {
        // Dolphin Scheduler项目名
        String dsProjectName =
            ProjectUtils.generateDolphinProjectName(requestRef.getWorkspaceName(), requestRef.getName());
        logger.info("begin to update project in Dolphin Scheduler, project is {}", dsProjectName);

        DolphinSchedulerProjectQueryOperation projectQueryOperation =
            new DolphinSchedulerProjectQueryOperation(this.baseUrl);
        Long projectId = projectQueryOperation.getProjectId(dsProjectName, Constant.DS_ADMIN_USERNAME);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("projectId", String.valueOf(projectId)));
        params.add(new BasicNameValuePair("projectName", dsProjectName));
        params.add(new BasicNameValuePair("description", requestRef.getDescription()));
        HttpEntity entity =
            EntityBuilder.create().setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))
                .setParameters(params).build();
        DolphinSchedulerHttpPost httpPost = new DolphinSchedulerHttpPost(projectUpdateUrl, Constant.DS_ADMIN_USERNAME);
        httpPost.setEntity(entity);

        DolphinSchedulerProjectResponseRef responseRef = new DolphinSchedulerProjectResponseRef();
        try (CloseableHttpResponse httpResponse =
            this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);) {
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);

            if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()
                && DolphinAppConnUtils.getCodeFromEntity(entString) == Constant.DS_RESULT_CODE_SUCCESS) {
                logger.info("Dolphin Scheduler更新项目 {} 成功, 返回的信息是 {}", dsProjectName,
                    DolphinAppConnUtils.getValueFromEntity(entString, "msg"));
            } else {
                throw new ExternalOperationFailedException(90026, "调度中心更新工程失败, 原因:" + entString);
            }
        } catch (ExternalOperationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90026, "调度中心更新项目失败", e);
        }

        DolphinSchedulerUserOperation userOperation = new DolphinSchedulerUserOperation(this.baseUrl);
        // 新增授权用户
        List<String> releaseUsersIncreased = (List<String>)requestRef.getParameter("releaseUsersIncreased");
        List<String> releaseUsersDecreased = (List<String>)requestRef.getParameter("releaseUsersDecreased");
        try {
            for (String userName : releaseUsersIncreased) {
                userOperation.grantProject(userName, projectId, false);
            }
            for (String userName : releaseUsersDecreased) {
                userOperation.grantProject(userName, projectId, true);
            }
        } catch (ExternalOperationFailedException e) {
            throw new ExternalOperationFailedException(90025, "调度中心授权出错", e);
        }

        return responseRef;
    }

    @Override
    public void setStructureService(StructureService service) {
        if (service instanceof DolphinSchedulerProjectService) {
            this.dolphinSchedulerProjectService = (DolphinSchedulerProjectService)service;
        }
    }
}
