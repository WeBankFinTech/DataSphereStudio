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
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * The type Dolphin scheduler project creation operation.
 *
 * @author yuxin.yuan
 * @date 2021/10/18
 */
public class DolphinSchedulerProjectCreationOperation implements ProjectCreationOperation, DolphinSchedulerConf {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerProjectCreationOperation.class);

    private static final Long DEFAULT_PROJECT_ID = 0L;

    private DolphinSchedulerProjectService dolphinSchedulerProjectService;

    private String baseUrl;

    private String projectUrl;

    private SSORequestOperation<DolphinSchedulerHttpPost, CloseableHttpResponse> postOperation;

    public DolphinSchedulerProjectCreationOperation() {
    }

    @Override
    public void init() {
        this.baseUrl = this.dolphinSchedulerProjectService.getAppInstance().getBaseUrl();
        this.projectUrl = baseUrl.endsWith("/") ? baseUrl + "projects/create" : baseUrl + "/projects/create";

        this.postOperation = new DolphinSchedulerPostRequestOperation(baseUrl);
    }

    @Override
    public void setStructureService(StructureService service) {
        if (service instanceof DolphinSchedulerProjectService) {
            this.dolphinSchedulerProjectService = (DolphinSchedulerProjectService)service;
        }
    }

    @Override
    public ProjectResponseRef createProject(ProjectRequestRef requestRef) throws ExternalOperationFailedException {
        // Dolphin Scheduler项目名
        String dsProjectName = ProjectUtils.generateDolphinProjectName(requestRef.getWorkspaceName(), requestRef.getName());
        logger.info("begin to create project in DolphinScheduler, project is {}", dsProjectName);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("projectName", dsProjectName));
        params.add(new BasicNameValuePair("description", requestRef.getDescription()));
        HttpEntity entity = EntityBuilder.create()
            .setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))
            .setParameters(params)
            .build();
        // 使用管理员账户创建项目
        DolphinSchedulerHttpPost httpPost = new DolphinSchedulerHttpPost(projectUrl, Constant.DS_ADMIN_USERNAME);
        httpPost.setEntity(entity);

        DolphinSchedulerProjectResponseRef responseRef = new DolphinSchedulerProjectResponseRef();
        try (CloseableHttpResponse httpResponse =
            this.postOperation.requestWithSSO(null, httpPost);) {
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
            logger.info("dolphin返回报文"+entString);

            if (HttpStatus.SC_CREATED == httpResponse.getStatusLine().getStatusCode()) {
                String codeString = DolphinAppConnUtils.getValueFromEntity(entString, "code");
                int code = Integer.parseInt(codeString);

                if (Constant.DS_RESULT_CODE_SUCCESS == code) {
                    logger.info("DolphinScheduler新建项目 {} 成功, 返回的信息是 {}", dsProjectName,
                        DolphinAppConnUtils.getValueFromEntity(entString, "msg"));
                } else if (Constant.DS_RESULT_CODE_PROJECT_ALREADY_EXISTS == code) {
                    logger.info("DolphinScheduler项目 {} 已经存在, 返回的信息是 {}", dsProjectName,
                        DolphinAppConnUtils.getValueFromEntity(entString, "msg"));
                    // 不返回project id
                    responseRef.setProjectRefId(DEFAULT_PROJECT_ID);
                    return responseRef;
                } else {
                    throw new ExternalOperationFailedException(90021, "新建工程失败, 原因:" + entString);
                }
            } else {
                throw new ExternalOperationFailedException(90021, "新建工程失败, 原因:" + entString);
            }
        } catch (final Exception e) {
            throw new ExternalOperationFailedException(90022, "failed to create project in DolphinScheduler", e);
        }

        // 需要授权的用户名
        List<String> releaseUsers = (List<String>)requestRef.getParameter("releaseUsers");

        DolphinSchedulerProjectQueryOperation projectQueryOperation =
            new DolphinSchedulerProjectQueryOperation(this.baseUrl);
        DolphinSchedulerUserOperation userOperation = new DolphinSchedulerUserOperation(this.baseUrl);
        try {
            Long projectId = projectQueryOperation.getProjectId(dsProjectName, Constant.DS_ADMIN_USERNAME);
            for (String userName : releaseUsers) {
                userOperation.grantProject(userName, projectId, false);
            }
        } catch (ExternalOperationFailedException e) {
            throw new ExternalOperationFailedException(90025, "新建工程成功，调度中心授权出错", e);
        }

        // 不返回project id
        responseRef.setProjectRefId(DEFAULT_PROJECT_ID);
        return responseRef;
    }

}
