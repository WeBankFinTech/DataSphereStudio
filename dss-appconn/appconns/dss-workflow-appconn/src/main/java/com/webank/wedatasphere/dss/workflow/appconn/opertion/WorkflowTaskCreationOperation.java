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

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

import com.webank.wedatasphere.dss.workflow.appconn.ref.WorkflowCreateRequestRef;
import com.webank.wedatasphere.dss.workflow.appconn.ref.WorkflowCreateResponseRef;
import com.webank.wedatasphere.dss.workflow.common.protocol.RequestCreateWorkflow;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseCreateWorkflow;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author allenlliu
 * @date 2020/10/21 02:47 PM
 */
public class WorkflowTaskCreationOperation implements RefCreationOperation<WorkflowCreateRequestRef, WorkflowCreateResponseRef> {


    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowTaskCreationOperation.class);

    private DevelopmentService developmentService;


    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

    @Override
    public WorkflowCreateResponseRef createRef(WorkflowCreateRequestRef workflowCreateRequestRef) throws ExternalOperationFailedException {
        //发送RPC请求
        WorkflowCreateResponseRef workflowCreateResponseRef = null;
        String userName = workflowCreateRequestRef.getUserName();
        String workflowName = workflowCreateRequestRef.getDSSOrchestratorInfo().getName();
        String contextIDStr = workflowCreateRequestRef.getContextIDStr() != null ?
                workflowCreateRequestRef.getContextIDStr() : "";
        String description = workflowCreateRequestRef.getDSSOrchestratorInfo().getDesc();
        List<DSSLabel> dssLabels = workflowCreateRequestRef.getDSSLabels();
        long parentFlowId = workflowCreateRequestRef.getParentFlowID() != null ? workflowCreateRequestRef.getParentFlowID() : -1L;
        List<String> linkedAppConnNames = workflowCreateRequestRef.getDSSOrchestratorInfo().getLinkedAppConnNames() != null ?
                workflowCreateRequestRef.getDSSOrchestratorInfo().getLinkedAppConnNames() : new ArrayList<>();
        String uses = workflowCreateRequestRef.getDSSOrchestratorInfo().getUses() != null ?
                workflowCreateRequestRef.getDSSOrchestratorInfo().getUses() : "uses";
        RequestCreateWorkflow requestCreateWorkflow = new RequestCreateWorkflow(userName, workflowName,
                contextIDStr, description, parentFlowId, uses, linkedAppConnNames, dssLabels);
        Sender sender = Sender.getSender("DSS-WORKFLOW-SERVER-DEV");
        if (null != sender) {
            ResponseCreateWorkflow responseCreateWorkflow = (ResponseCreateWorkflow) sender.ask(requestCreateWorkflow);
            workflowCreateResponseRef = new WorkflowCreateResponseRef();
            workflowCreateResponseRef.setOrchestratorId(responseCreateWorkflow.getDssFlow().getId());
            workflowCreateResponseRef.setContent(responseCreateWorkflow.getDssFlow().getFlowJson());
        } else {
            LOGGER.error("dss workflow server dev sender is null, can not send rpc message");
            DSSExceptionUtils.dealErrorException(61123,"dss workflow server dev sender is null", ExternalOperationFailedException.class);
        }
        return workflowCreateResponseRef;
    }
}
