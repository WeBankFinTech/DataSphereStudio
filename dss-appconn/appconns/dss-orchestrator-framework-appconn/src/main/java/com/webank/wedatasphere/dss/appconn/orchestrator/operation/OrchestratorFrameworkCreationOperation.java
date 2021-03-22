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

package com.webank.wedatasphere.dss.appconn.orchestrator.operation;

import com.webank.wedatasphere.dss.appconn.orchestrator.conf.OrchestratorConf;
import com.webank.wedatasphere.dss.appconn.orchestrator.ref.DefaultOrchestratorCreateResponseRef;
import com.webank.wedatasphere.dss.common.protocol.ResponseCreateOrchestrator;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestCreateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCreateRequestRef;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCreateResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by cooperyang on 2020/11/19
 * Description:
 */
public class OrchestratorFrameworkCreationOperation implements
        RefCreationOperation<OrchestratorCreateRequestRef, OrchestratorCreateResponseRef> {


    private final Sender sender = Sender.getSender(OrchestratorConf.ORCHESTRATOR_SERVER_DEV_NAME.getValue());

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorFrameworkCreationOperation.class);

    private DevelopmentService developmentService;

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

    @Override
    public OrchestratorCreateResponseRef createRef(OrchestratorCreateRequestRef requestRef) throws ExternalOperationFailedException {
        if (null == requestRef) {
            LOGGER.error("requestRef is null can not create Ref");
            return null;
        }
        RequestCreateOrchestrator createRequest = new RequestCreateOrchestrator(requestRef.getUserName(),
                requestRef.getWorkspaceName(), requestRef.getProjectName(),
                requestRef.getProjectId(), requestRef.getDSSOrchestratorInfo().getDesc(),
                requestRef.getDSSOrchestratorInfo(), requestRef.getDSSLabels());
        ResponseCreateOrchestrator createResponse = null;
        try {
            createResponse = (ResponseCreateOrchestrator) sender.ask(createRequest);
        } catch (Exception e) {
            DSSExceptionUtils.dealErrorException(60015, "create orchestrator ref failed", e,
                    ExternalOperationFailedException.class);
        }
        if (createResponse == null) {
            LOGGER.error("createResponse is null, can not get correct response");
            return null;
        }
        LOGGER.info("End to ask to create orchestrator, orcId is {} orcVersionId is {}",
                createResponse.orchestratorId(), createResponse.orchestratorVersionId());
        DefaultOrchestratorCreateResponseRef createResponseRef = new DefaultOrchestratorCreateResponseRef();
        createResponseRef.setOrchestratorId(createResponse.orchestratorId());
        createResponseRef.setOrchestratorVersionId(createResponse.orchestratorVersionId());
        return createResponseRef;
    }
}
