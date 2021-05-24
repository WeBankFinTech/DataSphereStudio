package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerFlow;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerProject;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.hooks.LinkisDolphinSchedulerProjectPublishHook;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.parser.DolphinSchedulerProjectParser;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerInstanceResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerGetRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpGet;
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
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectUploadToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.UploadToScheduleOperation;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The type Dolphin scheduler workflow upload operation.
 *
 * @author yuxin.yuan
 * @date 2021/05/24
 */
public class DolphinSchedulerWorkflowUploadOperation implements UploadToScheduleOperation<ProjectUploadToSchedulerRef> {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerWorkflowUploadOperation.class);

    private AppDesc appDesc;

    private List<DSSLabel> dssLabels;

    private Long processDefinitionId;

    private SSORequestOperation<DolphinSchedulerHttpPost, CloseableHttpResponse> postOperation;

    private SSORequestOperation<DolphinSchedulerHttpGet, CloseableHttpResponse> getOperation;

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private String saveProcessDefinitionUrl;

    private String listProcessDefinitionUrl;

    private String updateProcessDefinitionUrl;

    public DolphinSchedulerWorkflowUploadOperation(AppDesc appDesc) {
        this.appDesc = appDesc;
        init();
    }

    private void init() {
        String baseUrl = this.appDesc.getAppInstances().get(0).getBaseUrl();
        this.postOperation = new DolphinSchedulerPostRequestOperation(baseUrl);
        this.getOperation = new DolphinSchedulerGetRequestOperation(baseUrl);
        this.saveProcessDefinitionUrl = baseUrl.endsWith("/") ? baseUrl + "projects/${projectName}/process/save" :
            baseUrl + "/projects/${projectName}/process/save";
        this.listProcessDefinitionUrl = baseUrl.endsWith("/") ? baseUrl + "projects/${projectName}/process/list" :
            baseUrl + "/projects/${projectName}/process/list";
        this.updateProcessDefinitionUrl = baseUrl.endsWith("/") ? baseUrl + "projects/${projectName}/process/update" :
            baseUrl + "/projects/${projectName}/process/update";
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

    public Long getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(Long processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
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

    @Override
    public ResponseRef publish(ProjectUploadToSchedulerRef projectUploadToSchedulerRef)
        throws ExternalOperationFailedException {
        int processDefinitionId = publishDssWorkflow(projectUploadToSchedulerRef.getWorkspace(),
            projectUploadToSchedulerRef.getUserName(), projectUploadToSchedulerRef.getDSSProject(),
            projectUploadToSchedulerRef.getDSSFlowList(), this.processDefinitionId);
        return new DolphinSchedulerInstanceResponseRef(String.valueOf(processDefinitionId), 0);
    }

    private int publishDssWorkflow(Workspace workspace, String publishUser, DSSProject dssProject,
        List<DSSFlow> dssFlowList, Long processDefinitionId) throws ExternalOperationFailedException {
        ProjectParser projectParser = getProjectParser();
        ProjectTuning projectTuning = getProjectTuning();
        ProjectPublishHook[] projectPublishHooks = getProjectPublishHooks();
        SchedulerProject schedulerProject = projectParser.parseProject(dssProject, dssFlowList);
        projectTuning.tuningSchedulerProject(schedulerProject);
        Stream.of(projectPublishHooks).forEach(DSSExceptionUtils.handling(hook -> hook.prePublish(schedulerProject)));
        // 未发布过，则执行发布
        if (Objects.isNull(processDefinitionId)) {
            publish(schedulerProject);
            processDefinitionId = Long.valueOf(getProcessDefinitionIdByName(schedulerProject));
        } else {
            update(schedulerProject, processDefinitionId);
        }
        // 成功发布后，获取Dolphin Scheduler中工作流定义id
        Stream.of(projectPublishHooks).forEach(DSSExceptionUtils.handling(hook -> hook.postPublish(schedulerProject)));
        return processDefinitionId.intValue();
    }

    private void publish(SchedulerProject schedulerProject) throws ExternalOperationFailedException {
        DolphinSchedulerProject dolphinSchedulerProject = (DolphinSchedulerProject)schedulerProject;
        List<SchedulerFlow> schedulerFlows = dolphinSchedulerProject.getSchedulerFlows();
        if (schedulerFlows.size() != 1) {
            throw new ExternalOperationFailedException(90011, "发布的工作流数目不为1");
        }
        DolphinSchedulerFlow schedulerFlow = (DolphinSchedulerFlow)schedulerFlows.get(0);

        Gson gson = new Gson();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("processDefinitionJson", gson.toJson(schedulerFlow.getProcessDefinitionJson())));
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
        String saveUrl = StringUtils.replace(saveProcessDefinitionUrl, "${projectName}", projectName);
        DolphinSchedulerHttpPost httpPost = new DolphinSchedulerHttpPost(saveUrl,
            dolphinSchedulerProject.getCreateBy());
        httpPost.setEntity(entity);

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), "utf-8");

            if (HttpStatus.SC_CREATED == httpResponse.getStatusLine().getStatusCode()
                && DolphinAppConnUtils.getCodeFromEntity(entString) == 0) {
                logger.info("DSS发布工作流 {} 到Dolphin Scheduler项目 {} 成功, 返回的信息是 {}", schedulerFlow.getName(), projectName,
                    DolphinAppConnUtils.getValueFromEntity(entString, "msg"));
            } else {
                throw new ExternalOperationFailedException(90012, "发布工作流失败, 原因:" + entString);
            }
        } catch (final Throwable t) {
            SchedulisExceptionUtils.dealErrorException(90012, "failed to publish workflow in Dolphin Scheduler", t,
                ExternalOperationFailedException.class);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }
    }

    private int getProcessDefinitionIdByName(SchedulerProject schedulerProject)
        throws ExternalOperationFailedException {
        DolphinSchedulerProject dolphinSchedulerProject = (DolphinSchedulerProject)schedulerProject;
        List<SchedulerFlow> schedulerFlows = dolphinSchedulerProject.getSchedulerFlows();
        if (schedulerFlows.size() != 1) {
            throw new ExternalOperationFailedException(90011, "发布的工作流数目不为1");
        }
        DolphinSchedulerFlow schedulerFlow = (DolphinSchedulerFlow)schedulerFlows.get(0);

        String projectName = ProjectUtils.generateDolphinProjectName(
            dolphinSchedulerProject.getDSSProject().getWorkspaceName(), dolphinSchedulerProject.getName());
        String queryUrl = StringUtils.replace(this.listProcessDefinitionUrl, "${projectName}", projectName);
        DolphinSchedulerHttpGet httpGet = new DolphinSchedulerHttpGet(queryUrl, dolphinSchedulerProject.getCreateBy());

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = this.getOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpGet);
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), "utf-8");

            if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()
                && DolphinAppConnUtils.getCodeFromEntity(entString) == 0) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(entString);
                JsonNode dataNode = jsonNode.get("data");
                for (JsonNode node : dataNode) {
                    if (schedulerFlow.getName().equals(node.get("name").asText())) {
                        return node.get("id").asInt();
                    }
                }
            } else {
                throw new ExternalOperationFailedException(90013, "从Dolphin Scheduler获取工作流定义列表失败, 原因:" + entString);
            }
        } catch (final Throwable t) {
            SchedulisExceptionUtils.dealErrorException(90013,
                "failed to get process definition list from Dolphin Scheduler", t,
                ExternalOperationFailedException.class);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }
        throw new ExternalOperationFailedException(90014, "Dolphin Scheduler中没有工作流定义:" + schedulerFlow.getName());
    }

    private void update(SchedulerProject schedulerProject, Long processDefinitionId)
        throws ExternalOperationFailedException {
        DolphinSchedulerProject dolphinSchedulerProject = (DolphinSchedulerProject)schedulerProject;
        List<SchedulerFlow> schedulerFlows = dolphinSchedulerProject.getSchedulerFlows();
        if (schedulerFlows.size() != 1) {
            throw new ExternalOperationFailedException(90011, "发布的工作流数目不为1");
        }
        DolphinSchedulerFlow schedulerFlow = (DolphinSchedulerFlow)schedulerFlows.get(0);

        Gson gson = new Gson();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", String.valueOf(processDefinitionId)));
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
        String updateUrl = StringUtils.replace(updateProcessDefinitionUrl, "${projectName}", projectName);
        DolphinSchedulerHttpPost httpPost = new DolphinSchedulerHttpPost(updateUrl,
            dolphinSchedulerProject.getCreateBy());
        httpPost.setEntity(entity);

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), "utf-8");

            if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()
                && DolphinAppConnUtils.getCodeFromEntity(entString) == 0) {
                logger.info("DSS发布工作流 {} 到Dolphin Scheduler项目 {} 成功, 返回的信息是 {}", schedulerFlow.getName(), projectName,
                    DolphinAppConnUtils.getValueFromEntity(entString, "msg"));
            } else {
                throw new ExternalOperationFailedException(90012, "发布工作流失败, 原因:" + entString);
            }
        } catch (final Throwable t) {
            SchedulisExceptionUtils.dealErrorException(90012, "failed to publish workflow in Dolphin Scheduler", t,
                ExternalOperationFailedException.class);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }
    }

}
