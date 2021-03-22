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
import com.webank.wedatasphere.dss.appconn.orchestrator.ref.DefaultOrchestratorUpdateResponseRef;
import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestUpdateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponsePublishOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorUpdateRef;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorUpdateResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 * @author allenlliu
 */
public class OrchestratorFrameworkUpdateOperation implements
        RefUpdateOperation<OrchestratorUpdateRef, OrchestratorUpdateResponseRef> {

    private final Sender sender = Sender.getSender(OrchestratorConf.ORCHESTRATOR_SERVER_DEV_NAME.getValue());

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorFrameworkUpdateOperation.class);

    private DevelopmentService developmentService;



    @Override
    public OrchestratorUpdateResponseRef updateRef(OrchestratorUpdateRef requestRef) throws ExternalOperationFailedException {
        if (null == requestRef){
            LOGGER.error("request ref is null, can not deal with null ref");
            return null;
        }
        LOGGER.info("Begin to ask to update orchestrator, requestRef is {}", requestRef);
        RequestUpdateOrchestrator updateRequest = new RequestUpdateOrchestrator(requestRef.getUserName(),
                requestRef.getWorkspaceName(), requestRef.getOrchestratorInfo(), requestRef.getDSSLabels());
        ResponsePublishOrchestrator updateResponse = null;
        try{
            updateResponse = (ResponsePublishOrchestrator) sender.ask(updateRequest);
        }catch(final Exception e){
            DSSExceptionUtils.dealErrorException(60015, "update orchestrator ref failed", e,
                    ExternalOperationFailedException.class);
        }
        LOGGER.info("End to ask to update orchestrator, responseRef is {}", updateResponse);
        if (updateResponse == null) {
            LOGGER.error("to get updateResponse from orchestrator is null");
            return null;
        }
        OrchestratorUpdateResponseRef updateResponseRef = new DefaultOrchestratorUpdateResponseRef();
        updateResponseRef.setUpdateResult(JobStatus.Success.equals(updateResponse.getJobStatus()));
        return updateResponseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }
}
