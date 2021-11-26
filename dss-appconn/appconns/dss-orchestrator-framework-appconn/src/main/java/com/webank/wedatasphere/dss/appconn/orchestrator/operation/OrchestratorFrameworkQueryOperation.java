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

import com.webank.wedatasphere.dss.appconn.orchestrator.ref.DefaultOrchestratorQueryResponseRef;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestQueryOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseQueryOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorQueryRequestRef;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorQueryResponseRef;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrchestratorFrameworkQueryOperation implements RefQueryOperation<OrchestratorQueryRequestRef> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorFrameworkQueryOperation.class);

    private final Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender();
    private DevelopmentService developmentService;

    @Override
    public OrchestratorQueryResponseRef query(OrchestratorQueryRequestRef requestRef) throws ExternalOperationFailedException {
        if (null == requestRef) {
            LOGGER.error("request of query is null");
            return null;
        }
        LOGGER.info("Begin to ask to create orchestrator, requestRef is {}", requestRef);
        RequestQueryOrchestrator queryRequest = new RequestQueryOrchestrator(requestRef.getOrchestratorIdList());
        ResponseQueryOrchestrator queryResponse = null;
        try {
            queryResponse = (ResponseQueryOrchestrator) sender.ask(queryRequest);
        } catch (Exception e) {
            DSSExceptionUtils.dealErrorException(60015, "create orchestrator ref failed",
                    ExternalOperationFailedException.class);
        }
        if (queryResponse == null) {
            LOGGER.error("query response is null, it is a fatal error");
            return null;
        }
        LOGGER.info("End to ask to query orchestrator, responseRef is {}", queryResponse);
        OrchestratorQueryResponseRef queryResponseRef = new DefaultOrchestratorQueryResponseRef();
        queryResponseRef.setOrchestratorVoList(queryResponse.getOrchestratorVoes());
        return queryResponseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }
}
