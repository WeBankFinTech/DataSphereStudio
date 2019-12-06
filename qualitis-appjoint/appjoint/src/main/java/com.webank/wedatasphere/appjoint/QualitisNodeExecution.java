package com.webank.wedatasphere.appjoint;

import com.google.gson.Gson;
import com.webank.wedatasphere.Md5Utils;
import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.execution.common.CompletedNodeExecutionResponse;
import com.webank.wedatasphere.dss.appjoint.execution.common.NodeExecutionAction;
import com.webank.wedatasphere.dss.appjoint.execution.common.NodeExecutionState;
import com.webank.wedatasphere.dss.appjoint.execution.core.AppJointNode;
import com.webank.wedatasphere.dss.appjoint.execution.core.LongTermNodeExecution;
import com.webank.wedatasphere.dss.appjoint.execution.core.NodeContext;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import com.webank.wedatasphere.dss.common.entity.node.Node;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public class QualitisNodeExecution extends LongTermNodeExecution {

    private static final String SUBMIT_TASK_PATH = "/qualitis/outer/api/v1/execution";
    private static final String GET_TASK_STATUS_PATH = "/qualitis/outer/api/v1/application/{applicationId}/status/";
    private static final String GET_TASK_RESULT_PATH = "/qualitis/outer/api/v1/application/{applicationId}/result/";

    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisAppJoint.class);

    private String host = null;
    private Integer port = null;
    private String appId = null;
    private String appToken = null;

    private String baseUrl;

    public boolean canExecute(Node node, NodeContext nodeContext) {
        return true;
    }

    @Override
    public void init(Map<String, Object> map) throws AppJointErrorException {
        appId = (String) map.get("qualitis_appId");
        appToken = (String) map.get("qualitis_appToken");
        host = getHost();
        port = getPort();
    }

    @Override
    public boolean canExecute(AppJointNode appJointNode, NodeContext context, Session session) {
        return false;
    }

    @Override
    public NodeExecutionAction submit(AppJointNode appJointNode, NodeContext nodeContext, Session session) {
        try {
            Map map = nodeContext.getRuntimeMap();
            String filter = (String) map.get("filter");
            String executionUser = (String) map.get("executeUser");
            String createUser = (String) map.get("user");
            Long groupId = Long.valueOf((Integer) map.get("ruleGroupId"));

            QualitisSubmitRequest submitRequest = new QualitisSubmitRequest();
            submitRequest.setCreateUser(createUser);
            submitRequest.setExecutionUser(executionUser);
            submitRequest.setPartition(filter);
            submitRequest.setGroupId(groupId);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Object> entity = generateEntity(submitRequest);

            URI url = buildUrI(host, port, SUBMIT_TASK_PATH, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis()));
            String startLog = String.format("Start to submit job to qualitis. url: %s, method: %s, body: %s", url, javax.ws.rs.HttpMethod.POST, entity);
            LOGGER.info(startLog);
            nodeContext.appendLog(startLog);
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            String finishLog = String.format("Succeed to submit job to qualitis. response: %s", response);
            LOGGER.info(finishLog);
            nodeContext.appendLog(finishLog);

            if (response == null) {
                String errorMsg = "Error! Can not submit job, response is null";
                LOGGER.error(errorMsg);
                nodeContext.appendLog(errorMsg);
                return null;
            }

            if (!checkResponse(response)) {
                String message = (String) response.get("message");
                String errorMsg = String.format("Error! Can not submit job, exception: %s", message);
                LOGGER.error(errorMsg);
                nodeContext.appendLog(errorMsg);
                return null;
            }

            String applicationId = (String) ((Map<String, Object>) response.get("data")).get("application_id");
            return new QualitisNodeExecutionAction(applicationId);
        } catch (Exception e) {
            String errorMsg = "Error! Can not submit job";
            LOGGER.error(errorMsg, e);
            nodeContext.appendLog(errorMsg);
            return null;
        }
    }

    @Override
    public NodeExecutionState state(NodeExecutionAction nodeExecutionAction) {
        if (nodeExecutionAction == null) {
            return NodeExecutionState.Failed;
        }

        String applicationId = ((QualitisNodeExecutionAction)nodeExecutionAction).getApplicationId();
        try {
            // 发送请求查看任务的状态
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity entity = new HttpEntity(headers);

            String path = GET_TASK_STATUS_PATH.replace("{applicationId}", applicationId);
            URI url = buildUrI(host, port, path, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis()));
            LOGGER.info("Start to get job status. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
            Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
            LOGGER.info("Succeed to get job status. response: {}", response);

            if (response == null) {
                LOGGER.error("Error! Can not get job status, job_id: {}, response is null", applicationId);
                return NodeExecutionState.Failed;
            }

            if (!checkResponse(response)) {
                String message = (String) response.get("message");
                LOGGER.error("Error! Can not get job status, exception: {}", message);
                return NodeExecutionState.Failed;
            }

            List<Map<String, Object>> tasks = (List<Map<String, Object>>) ((Map<String, Object>) response.get("data")).get("task");
            Map<NodeExecutionState, Integer> statusCountMap = new HashMap<NodeExecutionState, Integer>(8);
            initCountMap(statusCountMap);
            Integer taskSize = tasks.size();
            for (Map<String, Object> task : tasks) {
                Integer taskStatus = (Integer) task.get("task_status");
                addStatus(taskStatus, statusCountMap);
            }

            Integer runningCount = statusCountMap.get(NodeExecutionState.Running);
            Integer successCount = statusCountMap.get(NodeExecutionState.Success);
            Integer failedCount = statusCountMap.get(NodeExecutionState.Failed);

            if (runningCount != 0) {
                return NodeExecutionState.Running;
            } else if (successCount.equals(taskSize)) {
                return NodeExecutionState.Success;
            } else if (failedCount != 0) {
                return NodeExecutionState.Failed;
            } else {
                return NodeExecutionState.Accepted;
            }
        } catch (Exception e) {
            LOGGER.error("Error! Can not get job status, job_id: {}", applicationId, e);
            return NodeExecutionState.Failed;
        }
    }

    @Override
    public CompletedNodeExecutionResponse result(NodeExecutionAction nodeExecutionAction, NodeContext nodeContext) {
        if (nodeExecutionAction == null) {
            return null;
        }

        String applicationId = ((QualitisNodeExecutionAction)nodeExecutionAction).getApplicationId();
        try {
            // Send request and get response
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity entity = new HttpEntity(headers);

            String path = GET_TASK_RESULT_PATH.replace("{applicationId}", applicationId);
            URI url = buildUrI(host, port, path, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis()));

            String startLog = String.format("Start to get job result. url: %s, method: %s, body: %s", url, HttpMethod.GET, entity);
            LOGGER.info(startLog);
            nodeContext.appendLog(startLog);
            Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
            String finishLog = String.format("Succeed to get job result. response: %s", response);
            LOGGER.info(finishLog);
            nodeContext.appendLog(finishLog);

            if (response == null) {
                String errorMsg = String.format("Error! Can not get job result, job_id: %s, response is null", applicationId);
                LOGGER.error(errorMsg);
                nodeContext.appendLog(errorMsg);
                return null;
            }

            if (!checkResponse(response)) {
                String message = (String) response.get("message");
                String errorMsg = String.format("Error! Can not get job result, exception: {}", message);
                LOGGER.error(errorMsg);
                nodeContext.appendLog(errorMsg);
                return null;
            }

            Integer passNum = (Integer) ((Map<String, Object>) response.get("data")).get("pass_num");
            Integer failedNum = (Integer) ((Map<String, Object>) response.get("data")).get("failed_num");
            Integer notPassNum = (Integer) ((Map<String, Object>) response.get("data")).get("not_pass_num");
            String resultMessage = (String) ((Map<String, Object>) response.get("data")).get("result_message");

            String taskMsg = String.format("Task result: Pass/Failed/Not Pass ------- %s/%s/%s", passNum, failedNum, notPassNum);
            LOGGER.info(taskMsg);
            LOGGER.info(resultMessage);
            nodeContext.appendLog(taskMsg);
            nodeContext.appendLog(resultMessage);

            CompletedNodeExecutionResponse result = new CompletedNodeExecutionResponse();
            if (failedNum != 0 || notPassNum != 0) {
                result.setIsSucceed(false);
            } else {
                result.setIsSucceed(true);
            }
            return result;
        } catch (Exception e) {
            String errorMsg = String.format("Error! Can not get job result, job_id: %s", applicationId);
            LOGGER.error(errorMsg, e);
            nodeContext.appendLog(errorMsg);
            return null;
        }
    }

    private void addStatus(Integer status, Map<NodeExecutionState, Integer> statusCountMap) {
        if (status.equals(QualitisTaskStatusEnum.SUBMITTED.getCode())) {
            statusCountMap.put(NodeExecutionState.Accepted, statusCountMap.get(NodeExecutionState.Accepted) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.INITED.getCode())) {
            statusCountMap.put(NodeExecutionState.Accepted, statusCountMap.get(NodeExecutionState.Accepted) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.RUNNING.getCode())) {
            statusCountMap.put(NodeExecutionState.Running, statusCountMap.get(NodeExecutionState.Running) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.SUCCEED.getCode())) {
            statusCountMap.put(NodeExecutionState.Success, statusCountMap.get(NodeExecutionState.Success) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.PASS_CHECKOUT.getCode())) {
            statusCountMap.put(NodeExecutionState.Success, statusCountMap.get(NodeExecutionState.Success) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.FAIL_CHECKOUT.getCode())) {
            statusCountMap.put(NodeExecutionState.Failed, statusCountMap.get(NodeExecutionState.Failed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.FAILED.getCode())) {
            statusCountMap.put(NodeExecutionState.Failed, statusCountMap.get(NodeExecutionState.Failed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.TASK_NOT_EXIST.getCode())) {
            statusCountMap.put(NodeExecutionState.Failed, statusCountMap.get(NodeExecutionState.Failed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.CANCELLED.getCode())) {
            statusCountMap.put(NodeExecutionState.Killed, statusCountMap.get(NodeExecutionState.Killed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.TIMEOUT.getCode())) {
            statusCountMap.put(NodeExecutionState.Failed, statusCountMap.get(NodeExecutionState.Failed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.SCHEDULED.getCode())) {
            statusCountMap.put(NodeExecutionState.Accepted, statusCountMap.get(NodeExecutionState.Accepted) + 1);
        }
    }

    private void initCountMap(Map<NodeExecutionState, Integer> statusCountMap) {
        statusCountMap.put(NodeExecutionState.Accepted, 0);
        statusCountMap.put(NodeExecutionState.Running, 0);
        statusCountMap.put(NodeExecutionState.Success, 0);
        statusCountMap.put(NodeExecutionState.Failed, 0);

    }

    private Boolean checkResponse(Map<String, Object> response) {
        String responseStatus = (String) response.get("code");
        return "200".equals(responseStatus);
    }

    private HttpEntity<Object> generateEntity(Object submitRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Gson gson = new Gson();
        return new HttpEntity<Object>(gson.toJson(submitRequest), headers);
    }

    private URI buildUrI(String host, int port, String path, String appId, String appToken,
                         String nonce, String timestamp) throws Exception {
        String signature = getSignature(appId, appToken, nonce, timestamp);
        String urlStr = "http://" + host + ":" + port;
        URI uri = UriBuilder.fromUri(urlStr)
                .path(path)
                .queryParam("app_id", appId)
                .queryParam("nonce", nonce)
                .queryParam("timestamp", timestamp)
                .queryParam("signature", signature)
                .build();
        return uri;
    }

    private String getSignature(String appId, String appToken, String nonce, String timestamp)
            throws Exception {
        return Md5Utils.getMd5L32(Md5Utils.getMd5L32(appId + nonce + timestamp) + appToken);
    }

    private Integer getPort() {
        String baseUrl = getBaseUrl();
        return UriBuilder.fromUri(baseUrl).build().getPort();
    }

    private String getHost() {
        String baseUrl = getBaseUrl();
        return UriBuilder.fromUri(baseUrl).build().getHost();
    }

    @Override
    public String getBaseUrl() {
        return this.baseUrl;
    }

    @Override
    public void setBaseUrl(String s) {
        this.baseUrl = s;
    }
}