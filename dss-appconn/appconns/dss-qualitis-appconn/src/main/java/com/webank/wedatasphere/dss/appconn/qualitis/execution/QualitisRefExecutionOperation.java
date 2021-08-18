/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.qualitis.execution;

import com.google.common.collect.Maps;
import com.netflix.ribbon.proxy.annotation.Http;
import com.webank.wedatasphere.dss.appconn.qualitis.QualitisAppConn;
import com.webank.wedatasphere.dss.appconn.qualitis.constant.QualitisTaskStatusEnum;
import com.webank.wedatasphere.dss.appconn.qualitis.model.QualitisGetAction;
import com.webank.wedatasphere.dss.appconn.qualitis.model.QualitisPostAction;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.CompletedExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.Killable;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.LongTermRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.Procedure;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QualitisRefExecutionOperation extends LongTermRefExecutionOperation implements Killable, Procedure {
    private static final String SUBMIT_TASK_PATH = "/qualitis/outer/api/v1/execution";
    private static final String GET_TASK_STATUS_PATH = "/qualitis/outer/api/v1/application/{applicationId}/status/";
    private static final String GET_TASK_RESULT_PATH = "/qualitis/outer/api/v1/application/{applicationId}/result/";
    private static final String KILL_TASK_PATH = "/qualitis/outer/api/v1/execution/application/kill/{applicationId}/{executionUser}";

    private static Logger logger = LoggerFactory.getLogger(QualitisRefExecutionOperation.class);

    private String appId = "linkis_id";
    private String appToken = "a33693de51";

    private DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    @Override
    public boolean kill(RefExecutionAction action) {
        if (null == action) {
            return false;
        }
        QualitisRefExecutionAction qualitisRefExecutionAction = (QualitisRefExecutionAction) action;
        String applicationId = qualitisRefExecutionAction.getApplicationId();
        String executionUser = qualitisRefExecutionAction.getExecutionUser();

        if (StringUtils.isEmpty(applicationId) || StringUtils.isEmpty(executionUser)) {
            return false;
        }

        String url = null;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), KILL_TASK_PATH.replace("{applicationId}", applicationId).replace("executionUser", executionUser), appId, appToken, RandomStringUtils.randomNumeric(5)
                , String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error! Can not kill job, job_id: {}", applicationId, e);
            return false;
        }
        QualitisGetAction qualitisGetAction = new QualitisGetAction();
        SSOUrlBuilderOperation ssoUrlBuilderOperation = qualitisRefExecutionAction.getSsoUrlBuilderOperation();
        ssoUrlBuilderOperation.setAppName(QualitisAppConn.QUALITIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);

        ssoUrlBuilderOperation.setWorkspace(qualitisRefExecutionAction.getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            qualitisGetAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, qualitisGetAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            logger.error("Kill Job Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        String errorMsg = "";
        if (code != 200) {
            errorMsg = header.toString();
            logger.error(errorMsg);
            return false;
        }
        return true;
    }

    @Override
    protected RefExecutionAction submit(ExecutionRequestRef requestRef) {
        try {
            Map jobContent = requestRef.getJobContent();

            Long groupId =  Long.parseLong(jobContent.get("rule_group_id").toString());
            String executionUser = String.valueOf(jobContent.get("execution_user"));
            String url = null;
            try {
                url = HttpUtils.buildUrI(getBaseUrl(), SUBMIT_TASK_PATH, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
                QualitisPostAction qualitisPostAction = new QualitisPostAction();

                qualitisPostAction.addRequestPayload("group_id", groupId);
                qualitisPostAction.addRequestPayload("execution_user", executionUser);

                SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
                ssoUrlBuilderOperation.setAppName(QualitisAppConn.QUALITIS_APPCONN_NAME);
                ssoUrlBuilderOperation.setReqUrl(url);

                ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
                Map<String, Object> resMap;
                HttpResult httpResult;
                String response = "";

                try {
                    qualitisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
                    httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, qualitisPostAction);
                    response = httpResult.getResponseBody();
                    resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
                } catch (Exception e) {
                    logger.error("Submit Qualitis Task Exception", e);
                    throw new ExternalOperationFailedException(90176, "Submit qualitis task exception", e);
                }
                Map<String, Object> header = (Map<String, Object>) resMap.get("header");
                int code = (int) header.get("code");
                String errorMsg = "";
                if (code != 200) {
                    errorMsg = header.toString();
                    throw new ExternalOperationFailedException(90176, errorMsg, null);
                }
                return new QualitisRefExecutionAction((String) resMap.get("application_id"), executionUser, ssoUrlBuilderOperation, requestRef.getWorkspace().getWorkspaceName());
            } catch (NoSuchAlgorithmException e) {
                logger.error("Submit Qualitis Task Exception", e);
                throw new ExternalOperationFailedException(90176, "Submit qualitis task by build url exception", e);
            }
        } catch (Exception e) {
            logger.error("Submit Qualitis Task Exception", e);
            return null;
        }
    }

    @Override
    public RefExecutionState state(RefExecutionAction action) {
        if (null == action) {
            return RefExecutionState.Failed;
        }
        QualitisRefExecutionAction qualitisRefExecutionAction = (QualitisRefExecutionAction) action;
        String applicationId = qualitisRefExecutionAction.getApplicationId();
        String executionUser = qualitisRefExecutionAction.getExecutionUser();

        if (StringUtils.isEmpty(applicationId) || StringUtils.isEmpty(executionUser)) {
            return RefExecutionState.Failed;
        }
        String url = null;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), GET_TASK_STATUS_PATH.replace("{applicationId}", applicationId), appId, appToken, RandomStringUtils.randomNumeric(5)
                , String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error! Can not get job status, job_id: {}", applicationId, e);
            return RefExecutionState.Failed;
        }
        QualitisGetAction qualitisGetAction = new QualitisGetAction();
        SSOUrlBuilderOperation ssoUrlBuilderOperation = qualitisRefExecutionAction.getSsoUrlBuilderOperation();
        ssoUrlBuilderOperation.setAppName(QualitisAppConn.QUALITIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);

        ssoUrlBuilderOperation.setWorkspace(qualitisRefExecutionAction.getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            qualitisGetAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, qualitisGetAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            logger.error("Get Job Status Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        String errorMsg = "";
        if (code != 200) {
            errorMsg = header.toString();
            logger.error("Failed to get status " + errorMsg);
            return RefExecutionState.Failed;
        }
        logger.info("Succeed to get job status. response: {}", response);
        List<Map<String, Object>> tasks = (List<Map<String, Object>>) ((Map<String, Object>) resMap.get("data")).get("task");
        Map<RefExecutionState, Integer> statusCountMap = new HashMap<RefExecutionState, Integer>(8);
        initCountMap(statusCountMap);
        Integer taskSize = tasks.size();
        for (Map<String, Object> task : tasks) {
            Integer taskStatus = (Integer) task.get("task_status");
            Boolean abortOnFailure = (Boolean) task.get("abort_on_failure");
            addStatus(taskStatus, abortOnFailure, statusCountMap);
        }

        Integer runningCount = statusCountMap.get(RefExecutionState.Running);
        Integer successCount = statusCountMap.get(RefExecutionState.Success);
        Integer failedCount = statusCountMap.get(RefExecutionState.Failed);

        if (runningCount != 0) {
            return RefExecutionState.Running;
        } else if (successCount.equals(taskSize)) {
            return RefExecutionState.Success;
        } else if (failedCount != 0) {
            return RefExecutionState.Failed;
        } else {
            return RefExecutionState.Accepted;
        }

    }

    private void addStatus(Integer status, Boolean abortOnFailure, Map<RefExecutionState, Integer> statusCountMap) {
        if (status.equals(QualitisTaskStatusEnum.SUBMITTED.getCode())) {
            statusCountMap.put(RefExecutionState.Accepted, statusCountMap.get(RefExecutionState.Accepted) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.INITED.getCode())) {
            statusCountMap.put(RefExecutionState.Accepted, statusCountMap.get(RefExecutionState.Accepted) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.RUNNING.getCode())) {
            statusCountMap.put(RefExecutionState.Running, statusCountMap.get(RefExecutionState.Running) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.SUCCEED.getCode())) {
            statusCountMap.put(RefExecutionState.Success, statusCountMap.get(RefExecutionState.Success) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.PASS_CHECKOUT.getCode())) {
            statusCountMap.put(RefExecutionState.Success, statusCountMap.get(RefExecutionState.Success) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.FAIL_CHECKOUT.getCode())) {
            if (abortOnFailure != null && abortOnFailure) {
                statusCountMap.put(RefExecutionState.Failed, statusCountMap.get(RefExecutionState.Failed) + 1);
            } else {
                statusCountMap.put(RefExecutionState.Success, statusCountMap.get(RefExecutionState.Success) + 1);
            }
        } else if (status.equals(QualitisTaskStatusEnum.FAILED.getCode())) {
            statusCountMap.put(RefExecutionState.Failed, statusCountMap.get(RefExecutionState.Failed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.TASK_NOT_EXIST.getCode())) {
            statusCountMap.put(RefExecutionState.Failed, statusCountMap.get(RefExecutionState.Failed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.CANCELLED.getCode())) {
            statusCountMap.put(RefExecutionState.Killed, statusCountMap.get(RefExecutionState.Killed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.TIMEOUT.getCode())) {
            statusCountMap.put(RefExecutionState.Failed, statusCountMap.get(RefExecutionState.Failed) + 1);
        } else if (status.equals(QualitisTaskStatusEnum.SCHEDULED.getCode())) {
            statusCountMap.put(RefExecutionState.Accepted, statusCountMap.get(RefExecutionState.Accepted) + 1);
        }

    }

    private void initCountMap(Map<RefExecutionState, Integer> statusCountMap) {
        statusCountMap.put(RefExecutionState.Accepted, 0);
        statusCountMap.put(RefExecutionState.Running, 0);
        statusCountMap.put(RefExecutionState.Success, 0);
        statusCountMap.put(RefExecutionState.Failed, 0);
    }

    @Override
    public CompletedExecutionResponseRef result(RefExecutionAction action) {
        if (null == action) {
            return new QualitisCompletedExecutionResponseRef(RefExecutionState.Failed.getCode(), "Application ID is null.");
        }

        QualitisRefExecutionAction qualitisRefExecutionAction = (QualitisRefExecutionAction) action;
        String applicationId = qualitisRefExecutionAction.getApplicationId();
        if (StringUtils.isEmpty(applicationId)) {
            return new QualitisCompletedExecutionResponseRef(RefExecutionState.Failed.getCode(), "Application ID is null.");
        }
        String url = null;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), GET_TASK_RESULT_PATH.replace("{applicationId}", applicationId), appId, appToken, RandomStringUtils.randomNumeric(5)
                , String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error! Can not get job result, job_id: {}", applicationId, e);
            return new QualitisCompletedExecutionResponseRef(RefExecutionState.Failed.getCode());
        }
        QualitisGetAction qualitisGetAction = new QualitisGetAction();
        SSOUrlBuilderOperation ssoUrlBuilderOperation = qualitisRefExecutionAction.getSsoUrlBuilderOperation();
        ssoUrlBuilderOperation.setAppName(QualitisAppConn.QUALITIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);

        ssoUrlBuilderOperation.setWorkspace(qualitisRefExecutionAction.getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            qualitisGetAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, qualitisGetAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            logger.error("Get Job Result Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        String errorMsg = "";
        if (code != 200) {
            errorMsg = header.toString();
            logger.error("Failed to get result " + errorMsg);
            return new QualitisCompletedExecutionResponseRef(RefExecutionState.Failed.getCode());
        }
        logger.info("Succeed to get job result. response: {}", response);
        Integer passNum = (Integer) ((Map<String, Object>) resMap.get("data")).get("pass_num");
        Integer failedNum = (Integer) ((Map<String, Object>) resMap.get("data")).get("failed_num");
        Integer notPassNum = (Integer) ((Map<String, Object>) resMap.get("data")).get("not_pass_num");
        String resultMessage = (String) ((Map<String, Object>) resMap.get("data")).get("result_message");

        String taskMsg = String.format("Task result: Pass/Failed/Not Pass ------- %s/%s/%s", passNum, failedNum, notPassNum);
        logger.info(taskMsg);
        logger.info(resultMessage);

        if (failedNum != 0) {
            return new QualitisCompletedExecutionResponseRef(RefExecutionState.Failed.getCode(), taskMsg + resultMessage);
        } else {
            return new QualitisCompletedExecutionResponseRef(RefExecutionState.Success.getCode(), taskMsg + resultMessage);
        }
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {    }

    @Override
    public float progress(RefExecutionAction action) {
        return 0.5f;
    }

    @Override
    public String log(RefExecutionAction action) {
        return null;
    }

    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl();
    }
}
