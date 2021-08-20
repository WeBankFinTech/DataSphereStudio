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

import com.webank.wedatasphere.dss.appconn.workflow.ref.WorkflowCopyRequestRef;
import com.webank.wedatasphere.dss.appconn.workflow.ref.WorkflowCopyResponseRef;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.workflow.common.protocol.RequestCopyWorkflow;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseCopyWorkflow;
import com.webank.wedatasphere.linkis.rpc.Sender;


public class WorkflowTaskCopyOperation implements RefCopyOperation<WorkflowCopyRequestRef> {
    private DevelopmentService service;

    private final Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender();

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.service = service;
    }

    @Override
    public WorkflowCopyResponseRef copyRef(WorkflowCopyRequestRef workflowCopyRequestRef) {
        Long appId = workflowCopyRequestRef.getAppId();
        Long orcVersionId = workflowCopyRequestRef.getOrcVersionId();
        String userName = workflowCopyRequestRef.getUserName();
        String workspaceName = workflowCopyRequestRef.getWorkspaceName();
        String contextIdStr = workflowCopyRequestRef.getContextID();
        String projectName = workflowCopyRequestRef.getProjectName();
        String version = workflowCopyRequestRef.getVersion();
        String description = workflowCopyRequestRef.getDescription();
        RequestCopyWorkflow requestCopyWorkflow = new RequestCopyWorkflow(userName,
                workspaceName, appId, contextIdStr,
                projectName, orcVersionId,
                version, description);
        ResponseCopyWorkflow responseCopyWorkflow = (ResponseCopyWorkflow) sender.ask(requestCopyWorkflow);
        WorkflowCopyResponseRef workflowCopyResponseRef = new WorkflowCopyResponseRef();
        workflowCopyResponseRef.setDssFlow(responseCopyWorkflow.getDssFlow());
        workflowCopyResponseRef.setCopyTargetAppId(responseCopyWorkflow.getDssFlow().getId());
        return workflowCopyResponseRef;
    }
}
