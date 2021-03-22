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

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.appconn.ref.WorkflowCopyRequestRef;
import com.webank.wedatasphere.dss.workflow.appconn.ref.WorkflowCopyResponseRef;
import com.webank.wedatasphere.dss.workflow.common.protocol.RequestCopyWorkflow;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseCopyWorkflow;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import com.webank.wedatasphere.linkis.rpc.Sender;

/**
 * @author allenlliu
 * @date 2020/10/21 05:23 PM
 */
public class WorkflowTaskCopyOperation implements RefCopyOperation<WorkflowCopyRequestRef, WorkflowCopyResponseRef> {

    private final String WORKFLOW_DEV_NAME = CommonVars.apply("wds.dss.workflow.dev.name", "dss-workflow-server-dev").getValue();


    private DevelopmentService service;

    private final Sender sender = Sender.getSender(WORKFLOW_DEV_NAME);


    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.service = service;
    }

    @Override
    public WorkflowCopyResponseRef copyRef(WorkflowCopyRequestRef workflowCopyRequestRef) throws ExternalOperationFailedException {
        Long appId = workflowCopyRequestRef.getAppId();
        Long orcVersionId = workflowCopyRequestRef.getOrcVersionId();
        String userName = workflowCopyRequestRef.getUserName();
        String workspaceName = workflowCopyRequestRef.getWorkspaceName();
        String contextIdStr = workflowCopyRequestRef.getContextIdStr();
        String projectName = workflowCopyRequestRef.getProjectName();
        String version = workflowCopyRequestRef.getVersion();
        String description = workflowCopyRequestRef.getDescription();
        RequestCopyWorkflow requestCopyWorkflow = new RequestCopyWorkflow(userName,
                workspaceName, appId, contextIdStr,
                projectName, orcVersionId,
                version, description);
        ResponseCopyWorkflow responseCopyWorkflow = (ResponseCopyWorkflow) sender.ask(requestCopyWorkflow);
        WorkflowCopyResponseRef workflowCopyResponseRef = new WorkflowCopyResponseRef("", 0);
        workflowCopyResponseRef.setDssFlow(responseCopyWorkflow.getDssFlow());
        workflowCopyResponseRef.setCopyTargetAppId(responseCopyWorkflow.getDssFlow().getId());
        return workflowCopyResponseRef;
    }
}
