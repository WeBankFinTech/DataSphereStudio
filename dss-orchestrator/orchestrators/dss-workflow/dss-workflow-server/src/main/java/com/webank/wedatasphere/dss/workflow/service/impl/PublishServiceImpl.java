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

package com.webank.wedatasphere.dss.workflow.service.impl;

import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerAppConn;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestFrameworkConvertOrchestration;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestFrameworkConvertOrchestrationStatus;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseConvertOrchestrator;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.dss.workflow.service.PublishService;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant.DEFAULT_SCHEDULER_APP_CONN;

public class PublishServiceImpl implements PublishService {

    private DSSFlowService dssFlowService;
    private WorkFlowParser workFlowParser;

    public void setDssFlowService(DSSFlowService dssFlowService) {
        this.dssFlowService = dssFlowService;
    }

    public void setWorkFlowParser(WorkFlowParser workFlowParser) {
        this.workFlowParser = workFlowParser;
    }

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected Sender getOrchestratorSender() {
        return DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender();
    }

    @Override
    public String submitPublish(String convertUser, Long workflowId,
        Map<String, Object> dssLabel, Workspace workspace, String comment) throws Exception {
        LOGGER.info("User {} begins to convert workflow {}.", convertUser, workflowId);
        //1 获取对应的orcId 和 orcVersionId
        //2.进行提交
        RequestFrameworkConvertOrchestration requestFrameworkConvertOrchestration = new RequestFrameworkConvertOrchestration();
        requestFrameworkConvertOrchestration.setComment(comment);
        requestFrameworkConvertOrchestration.setOrcAppId(workflowId);
        requestFrameworkConvertOrchestration.setUserName(convertUser);
        requestFrameworkConvertOrchestration.setWorkspace(workspace);
        DSSFlow dssFlow = null;
        try {
            dssFlow = dssFlowService.getFlow(workflowId);
            if(dssFlow == null) {
                DSSExceptionUtils.dealErrorException(63325, "workflow " + workflowId + " is not exists.", DSSErrorException.class);
                return null;
            }
            String schedulerAppConnName = workFlowParser.getValueWithKey(dssFlow.getFlowJson(), DSSWorkFlowConstant.SCHEDULER_APP_CONN_NAME);
            if(StringUtils.isBlank(schedulerAppConnName)) {
                // 向下兼容老版本
                schedulerAppConnName = DEFAULT_SCHEDULER_APP_CONN.getValue();
            }
            SchedulerAppConn schedulerAppConn = (SchedulerAppConn) AppConnManager.getAppConnManager().getAppConn(schedulerAppConnName);
            // 只是为了获取是否需要发布所有Orc，这里直接拿第一个AppInstance即可。
            AppInstance appInstance = schedulerAppConn.getAppDesc().getAppInstances().get(0);
            requestFrameworkConvertOrchestration.setConvertAllOrcs(schedulerAppConn.getOrCreateConversionStandard().getDSSToRelConversionService(appInstance).isConvertAllOrcs());
            requestFrameworkConvertOrchestration.setLabels(dssLabel);
            ResponseConvertOrchestrator response = (ResponseConvertOrchestrator) getOrchestratorSender().ask(requestFrameworkConvertOrchestration);
            return response.getId();
        } catch (DSSErrorException e) {
            throw e;
        } catch (final Exception t) {
            Object str = dssFlow == null ? workflowId : dssFlow.getName();
            LOGGER.error("Failed to submit publish {}.", str, t);
            DSSExceptionUtils.dealErrorException(63325, "Failed to submit publish " + str, t, DSSErrorException.class);
        }
        return null;
    }

    @Override
    public ResponseConvertOrchestrator getStatus(String username, String taskId) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("{} is asking status of {}.", username, taskId);
        }
        ResponseConvertOrchestrator response =new ResponseConvertOrchestrator();
        //通过rpc的方式去获取到最新status
        try {
            RequestFrameworkConvertOrchestrationStatus req = new RequestFrameworkConvertOrchestrationStatus(taskId);
            response = (ResponseConvertOrchestrator) getOrchestratorSender().ask(req);
            LOGGER.info("user {} gets status of {}, status is {}，msg is {}", username, taskId, response.getResponse().getJobStatus(), response.getResponse().getMessage());
        }catch (Exception t){
            LOGGER.error("failed to getStatus {} ", taskId, t);

        }
        return response;
    }

}
