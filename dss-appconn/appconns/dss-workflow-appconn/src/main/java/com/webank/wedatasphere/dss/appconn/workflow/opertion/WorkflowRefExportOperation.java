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

import com.webank.wedatasphere.dss.appconn.workflow.ref.WorkflowExportRequestRef;
import com.webank.wedatasphere.dss.appconn.workflow.ref.WorkflowExportResponseRef;
import com.webank.wedatasphere.dss.common.protocol.RequestExportWorkflow;
import com.webank.wedatasphere.dss.common.protocol.ResponseExportWorkflow;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WorkflowRefExportOperation implements RefExportOperation<WorkflowExportRequestRef> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowRefExportOperation.class);

    private DevelopmentService developmentService;


    @Override
    public WorkflowExportResponseRef exportRef(WorkflowExportRequestRef workflowExportRequestRef) throws ExternalOperationFailedException {

        String userName = workflowExportRequestRef.getUserName();
        //todo
        long flowId = workflowExportRequestRef.getAppId();
        Long projectId = workflowExportRequestRef.getProjectId();
        String projectName = workflowExportRequestRef.getProjectName();
        RequestExportWorkflow requestExportWorkflow = new RequestExportWorkflow(userName,
                flowId,
                projectId,
                projectName,
                BDPJettyServerHelper.gson().toJson(workflowExportRequestRef.getWorkspace()),
                workflowExportRequestRef.getDSSLabels());
        ResponseExportWorkflow responseExportWorkflow = null;
        try{
            Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender(workflowExportRequestRef.getDSSLabels());
            responseExportWorkflow = (ResponseExportWorkflow) sender.ask(requestExportWorkflow);
        }catch(final Exception t){
            DSSExceptionUtils.dealErrorException(60025, "failed to get rpc message", t, ExternalOperationFailedException.class);
        }
        if (null != responseExportWorkflow) {
            WorkflowExportResponseRef workflowExportResponseRef = new WorkflowExportResponseRef();
            workflowExportResponseRef.setFlowID(responseExportWorkflow.flowID());
            workflowExportResponseRef.setResourceId(responseExportWorkflow.resourceId());
            workflowExportResponseRef.setVersion(responseExportWorkflow.version());
            workflowExportResponseRef.addResponse("resourceId", responseExportWorkflow.resourceId());
            workflowExportResponseRef.addResponse("version", responseExportWorkflow.version());
            workflowExportResponseRef.addResponse("flowID", responseExportWorkflow.flowID());
            return workflowExportResponseRef;
        } else {
            throw new ExternalOperationFailedException(100085, "Error ask workflow to export!", null);
        }
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

}
