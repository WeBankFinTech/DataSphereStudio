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

import com.webank.wedatasphere.dss.common.protocol.RequestExportWorkflow;
import com.webank.wedatasphere.dss.common.protocol.ResponseExportWorkflow;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import org.apache.linkis.rpc.Sender;

import java.util.HashMap;
import java.util.Map;


public class WorkflowRefExportOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl, ExportResponseRef>
        implements RefExportOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl> {

    @Override
    public ExportResponseRef exportRef(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef) {

        String userName = requestRef.getUserName();
        long flowId = (long) requestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY);
        Long projectId = requestRef.getRefProjectId();
        String projectName = requestRef.getProjectName();
        RequestExportWorkflow requestExportWorkflow = new RequestExportWorkflow(userName,
                flowId,
                projectId,
                projectName,
                toJson(requestRef.getWorkspace()),
                requestRef.getDSSLabels());
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender(requestRef.getDSSLabels());
        ResponseExportWorkflow responseExportWorkflow = RpcAskUtils.processAskException(sender.ask(requestExportWorkflow),
                ResponseExportWorkflow.class, RequestExportWorkflow.class);
        Map<String, Object> resourceMap = new HashMap<>(2);
        resourceMap.put(ImportRequestRef.RESOURCE_ID_KEY, responseExportWorkflow.resourceId());
        resourceMap.put(ImportRequestRef.RESOURCE_VERSION_KEY, responseExportWorkflow.version());
        return ExportResponseRef.newBuilder().setResourceMap(resourceMap).success();
    }

}
