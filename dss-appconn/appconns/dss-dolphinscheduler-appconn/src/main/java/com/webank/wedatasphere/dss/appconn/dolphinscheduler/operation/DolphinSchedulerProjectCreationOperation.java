package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerProjectResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerProjectService;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpPost;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerPostRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinAppConnUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.SchedulisExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.SSOUrlBuilderOperationImpl;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
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

import java.util.ArrayList;
import java.util.List;

public class DolphinSchedulerProjectCreationOperation implements ProjectCreationOperation, DolphinSchedulerConf {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerProjectCreationOperation.class);

    private static final Long DEFAULT_PROJECT_ID = 0L;

    private DolphinSchedulerProjectService dolphinSchedulerProjectService;

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private SSORequestOperation<DolphinSchedulerHttpPost, CloseableHttpResponse> postOperation;

    private String projectUrl;

    public DolphinSchedulerProjectCreationOperation(DolphinSchedulerProjectService dolphinSchedulerProjectService) {
        this.dolphinSchedulerProjectService = dolphinSchedulerProjectService;
        init();
    }

    private void init() {
        this.ssoUrlBuilderOperation = new SSOUrlBuilderOperationImpl();
        this.ssoUrlBuilderOperation.setAppName(dolphinSchedulerProjectService.getAppDesc().getAppName());
        String baseUrl = this.dolphinSchedulerProjectService.getAppInstance().getBaseUrl();
        this.postOperation = new DolphinSchedulerPostRequestOperation(baseUrl);
        this.projectUrl = baseUrl.endsWith("/") ? baseUrl + "projects/create" : baseUrl + "/projects/create";
    }

    @Override
    public ProjectResponseRef createProject(ProjectRequestRef requestRef) throws ExternalOperationFailedException {
        // 在Dolphin Scheduler创建工程的时候
        String dsProjectName = ProjectUtils.generateDolphinProjectName(requestRef.getWorkspaceName(),
            requestRef.getName());
        logger.info("begin to create project in Dolphin Scheduler, project is {}", dsProjectName);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("projectName", dsProjectName));
        params.add(new BasicNameValuePair("description", requestRef.getDescription()));
        HttpEntity entity = EntityBuilder.create()
            .setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))
            .setParameters(params)
            .build();
        DolphinSchedulerHttpPost httpPost = new DolphinSchedulerHttpPost(projectUrl, requestRef.getCreateBy());
        httpPost.setEntity(entity);

        DolphinSchedulerProjectResponseRef responseRef = new DolphinSchedulerProjectResponseRef();
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), "utf-8");

            String code = DolphinAppConnUtils.getValueFromEntity(entString, "code");
            if (HttpStatus.SC_CREATED == httpResponse.getStatusLine().getStatusCode() && Integer.valueOf(code)
                .equals(0)) {
                logger.info("Dolphin Scheduler新建工程 {} 成功, 返回的信息是 {}", dsProjectName,
                    DolphinAppConnUtils.getValueFromEntity(entString, "msg"));
            } else {
                throw new ExternalOperationFailedException(90008,
                    "新建工程失败, 原因:" + IOUtils.toString(entity.getContent(), "utf-8"));
            }
            // 未返回project id
            responseRef.setProjectRefId(DEFAULT_PROJECT_ID);
        } catch (final Throwable t) {
            SchedulisExceptionUtils.dealErrorException(60051, "failed to create project in Dolphin Scheduler", t,
                ExternalOperationFailedException.class);
        } finally {
            IOUtils.closeQuietly(httpResponse);
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
