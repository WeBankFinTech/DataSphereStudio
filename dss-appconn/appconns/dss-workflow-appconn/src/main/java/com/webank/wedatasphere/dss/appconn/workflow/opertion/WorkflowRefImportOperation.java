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

import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.common.protocol.RequestImportWorkflow;
import com.webank.wedatasphere.dss.workflow.common.protocol.ResponseImportWorkflow;
import org.apache.linkis.rpc.Sender;

import java.util.HashMap;
import java.util.Map;

public class WorkflowRefImportOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.ImportWitContextRequestRefImpl, RefJobContentResponseRef>
        implements RefImportOperation<ThirdlyRequestRef.ImportWitContextRequestRefImpl> {

    @Override
    public RefJobContentResponseRef importRef(ThirdlyRequestRef.ImportWitContextRequestRefImpl requestRef) throws ExternalOperationFailedException {
        RequestImportWorkflow requestImportWorkflow = new RequestImportWorkflow(requestRef.getUserName(),
                (String) requestRef.getResourceMap().get(ImportRequestRef.RESOURCE_ID_KEY),
                (String) requestRef.getResourceMap().get(ImportRequestRef.RESOURCE_VERSION_KEY),
                requestRef.getRefProjectId(), requestRef.getProjectName(),
                requestRef.getNewVersion(),
                requestRef.getWorkspace(),
                requestRef.getContextId(), requestRef.getDSSLabels());

        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender(requestRef.getDSSLabels());
        ResponseImportWorkflow responseImportWorkflow = RpcAskUtils.processAskException(sender.ask(requestImportWorkflow),
                ResponseImportWorkflow.class, RequestImportWorkflow.class);
        if(responseImportWorkflow.getStatus() == JobStatus.Success) {
            if(MapUtils.isEmpty(responseImportWorkflow.getWorkflows())) {
                return RefJobContentResponseRef.newBuilder()
                        .error("Empty workflow returned from workflow server, please ask admin for help!");
            }
            Map<String, Object> refMap = new HashMap<>(2);
            responseImportWorkflow.getWorkflows().forEach((key, value) -> {
                refMap.put(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, key);
                refMap.put(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY, value);
            });
            return RefJobContentResponseRef.newBuilder().setRefJobContent(refMap).success();
        } else {
            return RefJobContentResponseRef.newBuilder().error("Unknown reason, please ask admin for help.");
        }
    }
}
