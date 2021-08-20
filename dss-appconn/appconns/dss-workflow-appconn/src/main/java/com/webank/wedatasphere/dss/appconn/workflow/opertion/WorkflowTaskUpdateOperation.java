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

import com.webank.wedatasphere.dss.appconn.workflow.ref.WorkflowUpdateRequestRef;
import com.webank.wedatasphere.dss.appconn.workflow.ref.WorkflowUpdateResponseRef;
import com.webank.wedatasphere.dss.common.protocol.RequestUpdateWorkflow;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseUpdateWorkflow;
import com.webank.wedatasphere.linkis.rpc.Sender;


public class WorkflowTaskUpdateOperation implements RefUpdateOperation<WorkflowUpdateRequestRef> {

    private DevelopmentService developmentService;

    private final Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender();

    @Override
    public WorkflowUpdateResponseRef updateRef(WorkflowUpdateRequestRef workflowUpdateRequestRef) throws ExternalOperationFailedException {
        WorkflowUpdateResponseRef workflowUpdateResponseRef = null;
        String userName = workflowUpdateRequestRef.getUserName();
        Long flowID = workflowUpdateRequestRef.getOrcId();
        String flowName = workflowUpdateRequestRef.getOrcName();
        String description = workflowUpdateRequestRef.getDescription();
        String uses = workflowUpdateRequestRef.getUses();
        RequestUpdateWorkflow requestUpdateWorkflow = new RequestUpdateWorkflow(userName, flowID, flowName, description, uses);
        if (null != sender) {
            ResponseUpdateWorkflow responseUpdateWorkflow = (ResponseUpdateWorkflow) sender.ask(requestUpdateWorkflow);
            workflowUpdateResponseRef = new WorkflowUpdateResponseRef();
            workflowUpdateResponseRef.setJobStatus(responseUpdateWorkflow.getJobStatus());
        } else {
            throw new ExternalOperationFailedException(100038, "Rpc sender is null", null);
        }
        return workflowUpdateResponseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }
}
