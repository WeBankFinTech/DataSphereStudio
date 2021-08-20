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

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.ref.DefaultOrchestratorCreateRequestRef;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CommonResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.common.protocol.RequestCreateWorkflow;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseCreateWorkflow;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class WorkflowTaskCreationOperation implements RefCreationOperation<DefaultOrchestratorCreateRequestRef> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowTaskCreationOperation.class);

    private Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender();

    private DevelopmentService developmentService;

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

    @Override
    public CommonResponseRef createRef(DefaultOrchestratorCreateRequestRef workflowCreateRequestRef) throws ExternalOperationFailedException {
        //发送RPC请求
        CommonResponseRef workflowCreateResponseRef = null;
        String userName = workflowCreateRequestRef.getUserName();
        String workflowName = workflowCreateRequestRef.getDSSOrchestratorInfo().getName();
        String contextIDStr = workflowCreateRequestRef.getContextID() != null ?
                workflowCreateRequestRef.getContextID() : "";
        String description = workflowCreateRequestRef.getDSSOrchestratorInfo().getDesc();
        List<DSSLabel> dssLabels = workflowCreateRequestRef.getDSSLabels();
        long parentFlowId = -1L;
        List<String> linkedAppConnNames = workflowCreateRequestRef.getDSSOrchestratorInfo().getLinkedAppConnNames() != null ?
                workflowCreateRequestRef.getDSSOrchestratorInfo().getLinkedAppConnNames() : new ArrayList<>();
        String uses = workflowCreateRequestRef.getDSSOrchestratorInfo().getUses() != null ?
                workflowCreateRequestRef.getDSSOrchestratorInfo().getUses() : "uses";
        RequestCreateWorkflow requestCreateWorkflow = new RequestCreateWorkflow(userName, workflowName,
                contextIDStr, description, parentFlowId, uses, linkedAppConnNames, dssLabels);

        if (null != sender) {
            ResponseCreateWorkflow responseCreateWorkflow = (ResponseCreateWorkflow) sender.ask(requestCreateWorkflow);
            workflowCreateResponseRef = new CommonResponseRef();
            workflowCreateResponseRef.setOrcId(responseCreateWorkflow.getDssFlow().getId());
            workflowCreateResponseRef.setContent(responseCreateWorkflow.getDssFlow().getFlowJson());
        } else {
            LOGGER.error("dss workflow server dev sender is null, can not send rpc message");
            DSSExceptionUtils.dealErrorException(61123,"dss workflow server dev sender is null", ExternalOperationFailedException.class);
        }
        return workflowCreateResponseRef;
    }
}
