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

package com.webank.wedatasphere.dss.appconn.workflow.opertion;

import com.webank.wedatasphere.dss.appconn.workflow.ref.WorkflowImportResponseRef;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.appconn.workflow.ref.WorkflowImportRequestRef;
import com.webank.wedatasphere.dss.workflow.common.protocol.RequestImportWorkflow;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseImportWorkflow;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowRefImportOperation implements
        RefImportOperation<WorkflowImportRequestRef> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowRefImportOperation.class);

    private DevelopmentService developmentService;

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

    @Override
    public WorkflowImportResponseRef importRef(WorkflowImportRequestRef requestRef) throws ExternalOperationFailedException {

        WorkflowImportResponseRef workflowImportResponseRef = null;
        RequestImportWorkflow requestImportWorkflow = new RequestImportWorkflow(requestRef.getUserName(),
                requestRef.getResourceId(), requestRef.getBmlVersion(),
                requestRef.getProjectId(), requestRef.getProjectName(),
                requestRef.getSourceEnv(), requestRef.getOrcVersion(),
                requestRef.getWorkspaceName(),
                DSSCommonUtils.COMMON_GSON.toJson(requestRef.getWorkspace()),
                requestRef.getContextID());

        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getSchedulerWorkflowSender();
        if (null != sender) {
            ResponseImportWorkflow responseImportWorkflow = (ResponseImportWorkflow) sender.ask(requestImportWorkflow);
            workflowImportResponseRef = new WorkflowImportResponseRef();
            if (responseImportWorkflow.getWorkflowIds() != null && responseImportWorkflow.getWorkflowIds().size() > 0){
                workflowImportResponseRef.setOrcId(responseImportWorkflow.getWorkflowIds().get(0));
            }else{
                LOGGER.error("failed to get ref orc id, workflow Ids are {}", responseImportWorkflow.getWorkflowIds());
            }
            workflowImportResponseRef.setStatus(responseImportWorkflow.getStatus());
        } else {
            throw new ExternalOperationFailedException(100039, "Rpc sender is null", null);
        }
        return workflowImportResponseRef;
    }
}
