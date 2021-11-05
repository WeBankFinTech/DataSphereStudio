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

import com.webank.wedatasphere.dss.appconn.workflow.ref.WorkflowDeleteRequestRef;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.protocol.RequestDeleteWorkflow;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import java.util.List;


public class WorkflowTaskDeletionOperation implements RefDeletionOperation<WorkflowDeleteRequestRef> {

    private DevelopmentService developmentService;

    @Override
    public void deleteRef(WorkflowDeleteRequestRef workflowDeleteRequestRef) throws ExternalOperationFailedException {
        String userName = workflowDeleteRequestRef.getUserName();
        Long flowId = workflowDeleteRequestRef.getAppId();
        RequestDeleteWorkflow requestDeleteWorkflow = new RequestDeleteWorkflow(userName, flowId);
        List<DSSLabel> dssLabels = workflowDeleteRequestRef.getDSSLabels();
        Sender tempSend = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender(dssLabels);
        if (null != tempSend) {
            tempSend.ask(requestDeleteWorkflow);
        } else {
            throw new ExternalOperationFailedException(100036, "Rpc sender is null", null);
        }
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }
}
