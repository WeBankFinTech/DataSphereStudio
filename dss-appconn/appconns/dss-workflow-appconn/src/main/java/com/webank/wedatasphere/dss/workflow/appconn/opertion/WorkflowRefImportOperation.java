/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.workflow.appconn.opertion;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.appconn.constant.WorkflowAppConnConstant;
import com.webank.wedatasphere.dss.workflow.appconn.ref.WorkflowImportRequestRef;
import com.webank.wedatasphere.dss.workflow.appconn.ref.WorkflowImportResponseRef;
import com.webank.wedatasphere.dss.workflow.common.protocol.RequestImportWorkflow;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseImportWorkflow;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author allenlliu
 * @date 2020/11/17 16:57
 */
public class WorkflowRefImportOperation implements
        RefImportOperation<WorkflowImportRequestRef, WorkflowImportResponseRef> {

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
                requestRef.getSourceEnv(), requestRef.getOrcVersion(), requestRef.getWorkspaceName(),
                DSSCommonUtils.COMMON_GSON.toJson(requestRef.getWorkspace())
        );
        Sender sender = Sender.getSender(WorkflowAppConnConstant.DSS_WORKFLOW_APPLICATION_NAME_PROD.getValue());
        if (null != sender) {
            ResponseImportWorkflow responseImportWorkflow = (ResponseImportWorkflow) sender.ask(requestImportWorkflow);
            workflowImportResponseRef = new WorkflowImportResponseRef();
            if (responseImportWorkflow.getWorkflowIds() != null && responseImportWorkflow.getWorkflowIds().size() > 0){
                workflowImportResponseRef.setRefOrcId(responseImportWorkflow.getWorkflowIds().get(0));
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
