package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.constant.Constant;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerConvertedRel;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerWorkflow;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerInstanceResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerGetRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpGet;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpPost;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerPostRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinAppConnUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.SchedulisExceptionUtils;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.DSSToRelConversionRequestRef;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.ProjectToRelConversionRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.CommonResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.common.utils.AppStandardClassUtils;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRelImpl;
import com.webank.wedatasphere.dss.workflow.conversion.operation.WorkflowToRelConversionOperation;
import com.webank.wedatasphere.dss.workflow.conversion.operation.WorkflowToRelConverter;
import com.webank.wedatasphere.dss.workflow.core.WorkflowFactory;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode;
import com.webank.wedatasphere.dss.workflow.core.json2flow.AbstractJsonToFlowParser;
import com.webank.wedatasphere.dss.workflow.core.json2flow.JsonToFlowParser;
import com.webank.wedatasphere.dss.workflow.core.json2flow.parser.WorkflowParser;
import org.apache.linkis.common.utils.JsonUtils;

/**
 * The type Dolphin scheduler workflow upload operation.
 *
 * @author yuxin.yuan
 * @date 2021/05/24
 */
public class DolphinSchedulerWorkflowUploadOperation extends WorkflowToRelConversionOperation {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerWorkflowUploadOperation.class);

    private List<WorkflowToRelConverter> workflowToRelConverters;

    private SSORequestOperation<DolphinSchedulerHttpPost, CloseableHttpResponse> postOperation;

    private SSORequestOperation<DolphinSchedulerHttpGet, CloseableHttpResponse> getOperation;

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private String saveProcessDefinitionUrl;

    private String listProcessDefinitionUrl;

    private String updateProcessDefinitionUrl;

    public DolphinSchedulerWorkflowUploadOperation() {

    }

    @Override
    public void init() {
        String appConnName = getConversionService().getAppStandard().getAppConnName();
        workflowToRelConverters =
            AppStandardClassUtils.getInstance(appConnName).getInstances(WorkflowToRelConverter.class).stream()
                .sorted(Comparator.comparingInt(WorkflowToRelConverter::getOrder)).collect(Collectors.toList());
        JsonToFlowParser parser = WorkflowFactory.INSTANCE.getJsonToFlowParser();
        if (parser instanceof AbstractJsonToFlowParser) {
            String packageName = WorkflowParser.class.getPackage().getName();
            List<WorkflowParser> workflowParsers =
                AppStandardClassUtils.getInstance(appConnName).getInstances(WorkflowParser.class).stream()
                    .filter(p -> !p.getClass().getName().startsWith(packageName)).collect(Collectors.toList());
            ((AbstractJsonToFlowParser)parser).addWorkflowParsers(workflowParsers);
        }

        String baseUrl = getConversionService().getAppInstance().getBaseUrl();

        this.postOperation = new DolphinSchedulerPostRequestOperation(baseUrl);
        this.getOperation = new DolphinSchedulerGetRequestOperation(baseUrl);

        this.saveProcessDefinitionUrl = baseUrl.endsWith("/") ? baseUrl + "projects/${projectName}/process/save"
            : baseUrl + "/projects/${projectName}/process/save";
        this.listProcessDefinitionUrl = baseUrl.endsWith("/") ? baseUrl + "projects/${projectName}/process/list"
            : baseUrl + "/projects/${projectName}/process/list";
        this.updateProcessDefinitionUrl = baseUrl.endsWith("/") ? baseUrl + "projects/${projectName}/process/update"
            : baseUrl + "/projects/${projectName}/process/update";
    }

    @Override
    public ResponseRef convert(DSSToRelConversionRequestRef ref) {
        List<Workflow> workflows;
        if (ref instanceof ProjectToRelConversionRequestRef) {
            ProjectToRelConversionRequestRef projectRef = (ProjectToRelConversionRequestRef)ref;
            // 一次只发布一个工作流
            workflows = projectRef.getDSSOrcList().stream().limit(1)
                .map(flow -> WorkflowFactory.INSTANCE.getJsonToFlowParser().parse((DSSFlow)flow))
                .collect(Collectors.toList());
        } else {
            return CommonResponseRef.error("Not support ref " + ref.getClass().getSimpleName());
        }

        ConvertedRel convertedRel = tryConvert(workflows, ref);

        Map<Long, Long> publishResult;
        try {
            publishResult = publish(convertedRel);
        } catch (ExternalOperationFailedException e) {
            logger.error("发布工作流失败，", e);
            return CommonResponseRef.error(e.getDesc());
        }

        try {
            return new DolphinSchedulerInstanceResponseRef(JsonUtils.jackson().writeValueAsString(publishResult), 0);
        } catch (JsonProcessingException e) {
            return CommonResponseRef.error("发布工作流失败，" + e.getMessage());
        }
    }

    @Override
    protected ConvertedRel tryConvert(List<Workflow> workflows, DSSToRelConversionRequestRef ref) {
        PreConversionRelImpl rel = new PreConversionRelImpl();
        rel.setWorkflows(workflows);
        rel.setDSSToRelConversionRequestRef(ref);
        ConvertedRel convertedRel = null;
        for (WorkflowToRelConverter workflowToRelConverter : workflowToRelConverters) {
            if (convertedRel == null) {
                convertedRel = workflowToRelConverter.convertToRel(rel);
            } else {
                convertedRel = workflowToRelConverter.convertToRel(convertedRel);
            }
        }
        return convertedRel;
    }

    public Map<Long, Long> publish(ConvertedRel convertedRel) throws ExternalOperationFailedException {
        DolphinSchedulerConvertedRel dolphinSchedulerConvertedRel = (DolphinSchedulerConvertedRel)convertedRel;
        ProjectToRelConversionRequestRef requestRef = dolphinSchedulerConvertedRel.getDSSToRelConversionRequestRef();

        List<Workflow> workflows = convertedRel.getWorkflows();
        Map<Long, Long> schedulerWorkflowIdMap = (Map<Long, Long>)requestRef.getParameter("schedulerWorkflowIdMap");

        for (Workflow workflow : workflows) {
            Long newProcessDefinitionId = publishDssWorkflow(requestRef.getUserName(), requestRef.getDSSProject(),
                workflow, schedulerWorkflowIdMap.get(workflow.getId()));
            schedulerWorkflowIdMap.put(workflow.getId(), newProcessDefinitionId);
        }

        return schedulerWorkflowIdMap;
    }

    private Long publishDssWorkflow(String publishUser, DSSProject dssProject, Workflow workflow,
        Long processDefinitionId) throws ExternalOperationFailedException {
        checkSchedulerProject(workflow);

        // 未发布过，则执行发布
        if (processDefinitionId == null) {
            return publish(dssProject, workflow, publishUser);
        } else {
            return update(dssProject, workflow, processDefinitionId, publishUser);
        }
    }

    private void checkSchedulerProject(Workflow flow) throws ExternalOperationFailedException {
        List<WorkflowNode> nodes = flow.getWorkflowNodes();
        for (WorkflowNode node : nodes) {
            DSSNode dssNode = node.getDSSNode();
            if (CollectionUtils.isEmpty(dssNode.getResources())) {
                throw new ExternalOperationFailedException(90021, dssNode.getName() + "节点内容不能为空");
            }
        }
    }

    private Long publish(DSSProject dssProject, Workflow workflow, String publishUser)
        throws ExternalOperationFailedException {
        DolphinSchedulerWorkflow schedulerFlow = (DolphinSchedulerWorkflow)workflow;

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
            dssProject.getWorkspaceName(), dssProject.getName());
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
            throw new ExternalOperationFailedException(90012, "发布工作流调用调度系统失败", e);
        }

        try {
            if (HttpStatus.SC_CREATED == httpStatusCode && DolphinAppConnUtils.getCodeFromEntity(entString) == 0) {
                logger.info("DSS发布工作流 {} 到DolphinScheduler项目 {} 成功，返回的信息是 {}", schedulerFlow.getName(), projectName,
                    entString);
                return Long.valueOf(getProcessDefinitionIdByName(dssProject, workflow));
            } else if (HttpStatus.SC_OK == httpStatusCode
                && DolphinAppConnUtils.getCodeFromEntity(entString) == 10105) {
                logger.info("DSS发布工作流 {} 到DolphinScheduler项目 {} 失败，该工作流已存在，返回的信息是 {}，执行更新操作", schedulerFlow.getName(),
                    projectName, entString);
                long schedulerWorkflowId = getProcessDefinitionIdByName(dssProject, workflow);
                return update(dssProject, workflow, schedulerWorkflowId, publishUser);
            } else {
                throw new ExternalOperationFailedException(90012,
                    "发布工作流失败，" + DolphinAppConnUtils.getValueFromJsonString(entString, "msg"));
            }
        } catch (IOException e) {
            throw new ExternalOperationFailedException(90013, "发布结果解析失败", e);
        }
    }

    private long getProcessDefinitionIdByName(DSSProject dssProject, Workflow workflow)
        throws ExternalOperationFailedException {
        DolphinSchedulerWorkflow schedulerFlow = (DolphinSchedulerWorkflow)workflow;

        String projectName = ProjectUtils.generateDolphinProjectName(
            dssProject.getWorkspaceName(), dssProject.getName());
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
                        return node.get("id").asLong();
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

    private Long update(DSSProject dssProject, Workflow workflow, Long processDefinitionId, String publishUser)
        throws ExternalOperationFailedException {

        DolphinSchedulerWorkflow schedulerFlow = (DolphinSchedulerWorkflow)workflow;

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
            dssProject.getWorkspaceName(), dssProject.getName());
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
            SchedulisExceptionUtils.dealErrorException(90012, "发布工作流调用调度系统失败", e,
                ExternalOperationFailedException.class);
        }

        try {
            if (HttpStatus.SC_OK == httpStatusCode) {
                if (DolphinAppConnUtils.getCodeFromEntity(entString) == 0) {
                    logger.info("DSS发布工作流 {} 到DolphinScheduler项目 {} ，更新成功，返回的信息是：{}", schedulerFlow.getName(),
                        projectName, entString);
                    return processDefinitionId;
                } else if (DolphinAppConnUtils.getCodeFromEntity(entString) == 50003) {
                    logger.info("DSS发布工作流 {} 到DolphinScheduler项目 {} ，更新失败：{}，执行创建操作", schedulerFlow.getName(),
                        projectName, entString);
                    return publish(dssProject, workflow, publishUser);
                } else if (DolphinAppConnUtils.getCodeFromEntity(entString) == 50008) {
                    logger.info("DSS发布工作流 {} 到DolphinScheduler项目 {} ，更新失败：{}，项目处于上线状态", schedulerFlow.getName(),
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
