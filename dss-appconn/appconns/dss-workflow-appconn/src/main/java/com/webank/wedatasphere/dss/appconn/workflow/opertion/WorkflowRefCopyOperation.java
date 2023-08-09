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

import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.workflow.common.protocol.RequestCopyWorkflow;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseCopyWorkflow;
import org.apache.linkis.rpc.Sender;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class WorkflowRefCopyOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.CopyWitContextRequestRefImpl, RefJobContentResponseRef>
        implements RefCopyOperation<ThirdlyRequestRef.CopyWitContextRequestRefImpl> {

    private final Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender();

    @Override
    public RefJobContentResponseRef copyRef(ThirdlyRequestRef.CopyWitContextRequestRefImpl workflowCopyRequestRef) {
        Long appId = (Long) workflowCopyRequestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY);
        String userName = workflowCopyRequestRef.getUserName();
        String contextIdStr = workflowCopyRequestRef.getContextId();
        String projectName = workflowCopyRequestRef.getProjectName();
        //插入version
        String version = workflowCopyRequestRef.getNewVersion();
        String description = (String) workflowCopyRequestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_DESCRIPTION);
        Long targetProjectId = workflowCopyRequestRef.getRefProjectId();
        Optional<Object> nodeSuffix = Optional.ofNullable(workflowCopyRequestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_NODE_SUFFIX));
        Optional<Object> newFlowName = Optional.ofNullable(workflowCopyRequestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_NAME));
        RequestCopyWorkflow requestCopyWorkflow = new RequestCopyWorkflow(userName,
                workflowCopyRequestRef.getWorkspace(), appId, contextIdStr,
                projectName, version, description, workflowCopyRequestRef.getDSSLabels(),
                targetProjectId, (String) nodeSuffix.orElse(null), (String) newFlowName.orElse(null));
        ResponseCopyWorkflow responseCopyWorkflow = RpcAskUtils.processAskException(sender.ask(requestCopyWorkflow),
                ResponseCopyWorkflow.class, RequestCopyWorkflow.class);
        Map<String, Object> refJobContent = new HashMap<>(2);
        refJobContent.put(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, responseCopyWorkflow.getDssFlow().getId());
        refJobContent.put(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY, responseCopyWorkflow.getDssFlow().getFlowJson());
        return RefJobContentResponseRef.newBuilder().setRefJobContent(refJobContent).success();
    }
}
