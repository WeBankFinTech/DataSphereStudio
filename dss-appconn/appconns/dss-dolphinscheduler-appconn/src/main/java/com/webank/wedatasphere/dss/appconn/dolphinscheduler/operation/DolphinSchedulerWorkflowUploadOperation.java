package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerFlow;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerProject;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.hooks.LinkisDolphinSchedulerProjectPublishHook;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.parser.DolphinSchedulerProjectParser;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerInstanceResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpPost;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerPostRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.tuning.DolphinSchedulerProjectTuning;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinAppConnUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.SchedulisExceptionUtils;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.ProjectPublishHook;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.ProjectParser;
import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.ProjectTuning;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectUploadToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.UploadToScheduleOperation;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DolphinSchedulerWorkflowUploadOperation implements UploadToScheduleOperation<ProjectUploadToSchedulerRef> {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerWorkflowUploadOperation.class);

    private AppDesc appDesc;

    private List<DSSLabel> dssLabels;

    private SSORequestOperation<DolphinSchedulerHttpPost, CloseableHttpResponse> postOperation;

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private String projectUrl;

    public DolphinSchedulerWorkflowUploadOperation(AppDesc appDesc) {
        this.appDesc = appDesc;
        init();
    }

    private void init() {
        String baseUrl = this.appDesc.getAppInstances().get(0).getBaseUrl();
        this.postOperation = new DolphinSchedulerPostRequestOperation(baseUrl);
        this.projectUrl = baseUrl.endsWith("/") ? baseUrl + "projects/${projectName}/process/save" :
            baseUrl + "/projects/${projectName}/process/save";
    }

    public AppDesc getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    public List<DSSLabel> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    @Override
    public ResponseRef publish(ProjectUploadToSchedulerRef projectUploadToSchedulerRef)
        throws ExternalOperationFailedException {
        publishDssWorkflow(projectUploadToSchedulerRef.getWorkspace(), projectUploadToSchedulerRef.getUserName(),
            projectUploadToSchedulerRef.getDSSProject(), projectUploadToSchedulerRef.getDSSFlowList());
        return new DolphinSchedulerInstanceResponseRef("success", 0);
    }

    private void publishDssWorkflow(Workspace workspace, String publishUser, DSSProject dssProject,
        List<DSSFlow> dssFlowList) throws ExternalOperationFailedException {
        ProjectParser projectParser = getProjectParser();
        ProjectTuning projectTuning = getProjectTuning();
        ProjectPublishHook[] projectPublishHooks = getProjectPublishHooks();
        SchedulerProject schedulerProject = projectParser.parseProject(dssProject, dssFlowList);
        projectTuning.tuningSchedulerProject(schedulerProject);
        Stream.of(projectPublishHooks).forEach(DSSExceptionUtils.handling(hook -> hook.prePublish(schedulerProject)));
        publish(schedulerProject);
        Stream.of(projectPublishHooks).forEach(DSSExceptionUtils.handling(hook -> hook.postPublish(schedulerProject)));
    }

    private void publish(SchedulerProject schedulerProject) throws ExternalOperationFailedException {
        DolphinSchedulerProject dolphinSchedulerProject = (DolphinSchedulerProject)schedulerProject;
        List<SchedulerFlow> schedulerFlows = dolphinSchedulerProject.getSchedulerFlows();
        if (schedulerFlows.size() != 1) {
            throw new ExternalOperationFailedException(20001, "发布工作流数不为1");
        }
        DolphinSchedulerFlow schedulerFlow = (DolphinSchedulerFlow)schedulerFlows.get(0);

        Gson gson = new Gson();
        List<NameValuePair> params = new ArrayList<>();
        params.add(
            new BasicNameValuePair("processDefinitionJson", gson.toJson(schedulerFlow.getProcessDefinitionJson())));
        params.add(new BasicNameValuePair("name", schedulerFlow.getName()));
        params.add(new BasicNameValuePair("description", schedulerFlow.getDescription()));
        params.add(new BasicNameValuePair("locations", gson.toJson(schedulerFlow.getLocations())));
        params.add(new BasicNameValuePair("connects", gson.toJson(schedulerFlow.getConnects())));
        HttpEntity entity = EntityBuilder.create()
            .setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))
            .setParameters(params)
            .build();
        String projectName = ProjectUtils.generateDolphinProjectName(
            dolphinSchedulerProject.getDSSProject().getWorkspaceName(), dolphinSchedulerProject.getName());
        projectUrl = StringUtils.replace(projectUrl, "${projectName}", projectName);
        DolphinSchedulerHttpPost httpPost = new DolphinSchedulerHttpPost(projectUrl,
            dolphinSchedulerProject.getCreateBy());
        httpPost.setEntity(entity);

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), "utf-8");

            String code = DolphinAppConnUtils.getValueFromEntity(entString, "code");
            if (HttpStatus.SC_CREATED == httpResponse.getStatusLine().getStatusCode() && Integer.valueOf(code)
                .equals(0)) {
                logger.info("DSS发布工作流 {} 到Dolphin Scheduler项目 {} 成功, 返回的信息是 {}", schedulerFlow.getName(), projectName,
                    DolphinAppConnUtils.getValueFromEntity(entString, "msg"));
            } else {
                throw new ExternalOperationFailedException(90008,
                    "发布工作流失败, 原因:" + IOUtils.toString(entity.getContent(), "utf-8"));
            }
        } catch (final Throwable t) {
            SchedulisExceptionUtils.dealErrorException(60051, "failed to publish workflow in Dolphin Scheduler", t,
                ExternalOperationFailedException.class);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }
    }

    public ProjectParser getProjectParser() {
        return new DolphinSchedulerProjectParser();
    }

    public ProjectTuning getProjectTuning() {
        return new DolphinSchedulerProjectTuning();
    }

    public ProjectPublishHook[] getProjectPublishHooks() {
        return new ProjectPublishHook[] {new LinkisDolphinSchedulerProjectPublishHook()};
    }

    private void uploadProject(Workspace workspace, String tmpSavePath, String projectName, String releaseUser)
        throws Exception {

        //todo cooperyang 由于schedulis还没有进行sso的改造，先沿用过去的方案进行upload

        Cookie cookie = null; //this.schedulisSecurityService.login(releaseUser);
        HttpPost httpPost = new HttpPost(projectUrl + "?project=" + projectName);
        httpPost.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
        CloseableHttpResponse response = null;
        File file = new File(tmpSavePath);
        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(cookie);
        CloseableHttpClient httpClient = null;
        InputStream inputStream = null;
        try {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.addBinaryBody("file", file);
            entityBuilder.addTextBody("ajax", "upload");
            entityBuilder.addTextBody("project", projectName);
            httpPost.setEntity(entityBuilder.build());
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            inputStream = httpEntity.getContent();
            String entStr = null;
            entStr = IOUtils.toString(inputStream, "utf-8");
            if (response.getStatusLine().getStatusCode() != 200) {
                logger.error("调用azkaban上传接口的返回不为200, status code 是 {}", response.getStatusLine().getStatusCode());
                throw new ErrorException(90013, "release project failed, " + entStr);
            }
            logger.info("upload project:{} success!", projectName);
        } catch (Exception e) {
            logger.error("upload failed,reason:", e);
            throw new ErrorException(90014, e.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
    }

    public String zipProject(String projectPath) throws DSSErrorException {
        return ZipHelper.zip(projectPath);
    }

    private String getSchedulisProjectCreateUrl(String projectName) {
        try {
            List<AppInstance> appInstances = this.appDesc.getAppInstancesByLabels(dssLabels);

            if (appInstances == null || appInstances.size() <= 0) {
                logger.error("appInstances are null, can not get schedulis project url");
                return null;
            }
            AppInstance appInstance = appInstances.get(0);

            String projectUploadUrl = appInstance.getBaseUrl() + "?project=" + projectName;

            logger.info("getSchedulisProjectCreateUrl is " + projectUploadUrl);

            return projectUploadUrl;
        } catch (final Exception e) {
            logger.error("Failed to get schedulis project create url for {}", projectName, e);
            return null;
        }
    }

}
