package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.constant.Constant;
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
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.ProjectPublishHook;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.ProjectParser;
import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.ProjectTuning;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
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
        int newProcessDefinitionId = publishDssWorkflow(projectUploadToSchedulerRef.getWorkspace(),
            projectUploadToSchedulerRef.getUserName(), projectUploadToSchedulerRef.getDSSProject(),
            projectUploadToSchedulerRef.getDSSFlowList(), this.processDefinitionId);
        return new DolphinSchedulerInstanceResponseRef(String.valueOf(newProcessDefinitionId), 0);
    }

    private int publishDssWorkflow(Workspace workspace, String publishUser, DSSProject dssProject,
        List<DSSFlow> dssFlowList, Long processDefinitionId) throws ExternalOperationFailedException {
        ProjectParser projectParser = getProjectParser();
        ProjectTuning projectTuning = getProjectTuning();
        ProjectPublishHook[] projectPublishHooks = getProjectPublishHooks();
        SchedulerProject schedulerProject = projectParser.parseProject(dssProject, dssFlowList);
        checkSchedulerProject(schedulerProject);
        projectTuning.tuningSchedulerProject(schedulerProject);
        Stream.of(projectPublishHooks).forEach(DSSExceptionUtils.handling(hook -> hook.prePublish(schedulerProject)));
        // 未发布过，则执行发布
        if (processDefinitionId == null) {
            processDefinitionId = publish(schedulerProject, publishUser);
        } else {
            processDefinitionId = update(schedulerProject, processDefinitionId, publishUser);
        }
        // 成功发布后，获取Dolphin Scheduler中工作流定义id
        Stream.of(projectPublishHooks).forEach(DSSExceptionUtils.handling(hook -> hook.postPublish(schedulerProject)));
        return processDefinitionId.intValue();
    }

    private void checkSchedulerProject(SchedulerProject schedulerProject) throws ExternalOperationFailedException {
        DolphinSchedulerProject dolphinSchedulerProject = (DolphinSchedulerProject)schedulerProject;
        List<SchedulerFlow> schedulerFlows = dolphinSchedulerProject.getSchedulerFlows();
        for (SchedulerFlow flow : schedulerFlows) {
            List<SchedulerNode> schedulerNodes = flow.getSchedulerNodes();
            for (SchedulerNode node : schedulerNodes) {
                DSSNode dssNode = node.getDSSNode();
                if (CollectionUtils.isEmpty(dssNode.getResources())) {
                    throw new ExternalOperationFailedException(90021, dssNode.getName() + "节点内容不能为空");
                }
            }
        }
    }

    private Long publish(SchedulerProject schedulerProject, String publishUser)
        throws ExternalOperationFailedException {
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
        DolphinSchedulerHttpPost httpPost = new DolphinSchedulerHttpPost(saveUrl, publishUser);
        httpPost.setEntity(entity);

        String entString = null;
        int httpStatusCode = 0;
        try (CloseableHttpResponse httpResponse =
            this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost)) {
            HttpEntity ent = httpResponse.getEntity();
            entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
            httpStatusCode = httpResponse.getStatusLine().getStatusCode();
        } catch (ExternalOperationFailedException e) {
            throw e;
        } catch (final Exception e) {
            SchedulisExceptionUtils.dealErrorException(90012, "发布工作流失败", e, ExternalOperationFailedException.class);
        }

        try {
            if (HttpStatus.SC_CREATED == httpStatusCode && DolphinAppConnUtils.getCodeFromEntity(entString) == 0) {
                logger.info("DSS发布工作流 {} 到Dolphin Scheduler项目 {} 成功，返回的信息是 {}", schedulerFlow.getName(), projectName,
                    entString);
                return Long.valueOf(getProcessDefinitionIdByName(schedulerProject));
            } else {
                throw new ExternalOperationFailedException(90012,
                    "发布工作流失败，" + DolphinAppConnUtils.getValueFromJsonString(entString, "msg"));
            }
        } catch (IOException e) {
            throw new ExternalOperationFailedException(90013, "发布结果解析失败", e);
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
        DolphinSchedulerHttpGet httpGet = new DolphinSchedulerHttpGet(queryUrl, Constant.DS_ADMIN_USERNAME);

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = this.getOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpGet);
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);

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
                throw new ExternalOperationFailedException(90014,
                    "从调度中心获取工作流信息失败, " + DolphinAppConnUtils.getValueFromEntity(entString, "msg"));
            }
        } catch (ExternalOperationFailedException e) {
            throw e;
        } catch (final Exception e) {
            SchedulisExceptionUtils.dealErrorException(90014, "从调度中心获取工作流信息失败", e,
                ExternalOperationFailedException.class);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }
        throw new ExternalOperationFailedException(90014, "从调度中心获取工作流信息失败");
    }

    private Long update(SchedulerProject schedulerProject, Long processDefinitionId, String publishUser)
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
        DolphinSchedulerHttpPost httpPost = new DolphinSchedulerHttpPost(updateUrl, publishUser);
        httpPost.setEntity(entity);

        String entString = null;
        int httpStatusCode = 0;
        try (CloseableHttpResponse httpResponse =
            this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost)) {
            HttpEntity ent = httpResponse.getEntity();
            entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
            httpStatusCode = httpResponse.getStatusLine().getStatusCode();
        } catch (ExternalOperationFailedException e) {
            throw e;
        } catch (final Exception e) {
            SchedulisExceptionUtils.dealErrorException(90012, "发布工作流失败", e,
                ExternalOperationFailedException.class);
        }

        try {
            if (HttpStatus.SC_OK == httpStatusCode) {
                if (DolphinAppConnUtils.getCodeFromEntity(entString) == 0) {
                    logger.info("DSS发布工作流 {} 到Dolphin Scheduler项目 {} ，更新成功，返回的信息是：{}", schedulerFlow.getName(),
                        projectName, entString);
                    return processDefinitionId;
                } else if (DolphinAppConnUtils.getCodeFromEntity(entString) == 50003) {
                    logger.info("DSS发布工作流 {} 到Dolphin Scheduler项目 {} ，更新失败：{}，执行创建操作", schedulerFlow.getName(),
                        projectName, entString);
                    return publish(schedulerProject, publishUser);
                } else if (DolphinAppConnUtils.getCodeFromEntity(entString) == 50008) {
                    logger.info("DSS发布工作流 {} 到Dolphin Scheduler项目 {} ，更新失败：{}，项目处于上线状态", schedulerFlow.getName(),
                        projectName, entString);
                    throw new ExternalOperationFailedException(90012, "该工作流在调度中心处于上线状态");
                }
            }
            throw new ExternalOperationFailedException(90012,
                "发布工作流失败, " + DolphinAppConnUtils.getValueFromEntity(entString, "msg"));
        } catch (IOException e) {
            throw new ExternalOperationFailedException(90013, "发布结果解析失败", e);
        }
    }

}
