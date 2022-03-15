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

import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestQueryOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseQueryOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorQueryResponseRef;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.rpc.Sender;

import java.util.List;

public class OrchestratorFrameworkQueryOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl, OrchestratorQueryResponseRef>
        implements RefQueryOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl, OrchestratorQueryResponseRef> {

    private final Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender();

    @Override
    public OrchestratorQueryResponseRef query(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        logger.info("Begin to ask to create orchestrator, requestRef is {}.", toJson(requestRef));
        List<Long> orchestratorIds = (List<Long>) requestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATOR_ID_LIST_KEY);
        RequestQueryOrchestrator queryRequest = new RequestQueryOrchestrator(orchestratorIds);
        ResponseQueryOrchestrator queryResponse = (ResponseQueryOrchestrator) sender.ask(queryRequest);
        logger.info("End to ask to query orchestrator, responseRef is {}.", toJson(queryResponse.getOrchestratorVoes()));
        return OrchestratorQueryResponseRef.newBuilder().setOrchestratorVoList(queryResponse.getOrchestratorVoes()).success();
    }
}
