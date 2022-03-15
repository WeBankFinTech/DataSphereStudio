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

package com.webank.wedatasphere.dss.appconn.orchestrator.operation;

import com.webank.wedatasphere.dss.common.protocol.ResponseCreateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestCreateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.rpc.Sender;

import java.util.HashMap;
import java.util.Map;

public class OrchestratorFrameworkCreationOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.DSSJobContentRequestRefImpl, RefJobContentResponseRef>
        implements RefCreationOperation<ThirdlyRequestRef.DSSJobContentRequestRefImpl> {

    private final Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender();

    @Override
    public RefJobContentResponseRef createRef(ThirdlyRequestRef.DSSJobContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        DSSOrchestratorInfo dssOrchestratorInfo = (DSSOrchestratorInfo) requestRef.getDSSJobContent().get(OrchestratorRefConstant.DSS_ORCHESTRATOR_INFO_KEY);
        RequestCreateOrchestrator createRequest = new RequestCreateOrchestrator(requestRef.getUserName(),
                requestRef.getWorkspace().getWorkspaceName(), requestRef.getProjectName(),
                requestRef.getProjectRefId(), dssOrchestratorInfo.getDesc(),
                dssOrchestratorInfo, requestRef.getDSSLabels());
        ResponseCreateOrchestrator createResponse = (ResponseCreateOrchestrator) sender.ask(createRequest);
        if (createResponse == null) {
            logger.error("Ask orchestrator framework to create orchestrator failed, returned null. Create request is {}.", createRequest);
            throw new ExternalOperationFailedException(50700, "Ask orchestrator framework to create orchestrator failed, returned null.");
        }
        logger.info("End to ask to create orchestrator, orcId is {} orcVersionId is {}.",
                createResponse.orchestratorId(), createResponse.orchestratorVersionId());
        Map<String, Object> orchestratorContent = new HashMap<>(2);
        orchestratorContent.put(OrchestratorRefConstant.ORCHESTRATOR_ID_KEY, createResponse.orchestratorId());
        orchestratorContent.put(OrchestratorRefConstant.ORCHESTRATOR_VERSION_ID_KEY, createResponse.orchestratorVersionId());
        return RefJobContentResponseRef.newBuilder().setRefJobContent(orchestratorContent).success();
    }

}
