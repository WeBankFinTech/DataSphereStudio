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

import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestUpdateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorUpdateRef;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CommonResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrchestratorFrameworkUpdateOperation implements
        RefUpdateOperation<OrchestratorUpdateRef> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorFrameworkUpdateOperation.class);

    @Override
    public CommonResponseRef updateRef(OrchestratorUpdateRef requestRef) throws ExternalOperationFailedException {
        if (null == requestRef){
            LOGGER.error("request ref is null, can not deal with null ref");
            return null;
        }
        LOGGER.info("Begin to ask to update orchestrator, requestRef is {}", requestRef);
        RequestUpdateOrchestrator updateRequest = new RequestUpdateOrchestrator(requestRef.getUserName(),
                requestRef.getWorkspaceName(), requestRef.getOrchestratorInfo(), requestRef.getDSSLabels());
        ResponseOperateOrchestrator updateResponse = null;
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender(requestRef.getDSSLabels());
        try{
            updateResponse = (ResponseOperateOrchestrator) sender.ask(updateRequest);
        }catch(final Exception e){
            DSSExceptionUtils.dealErrorException(60015, "update orchestrator ref failed", e,
                    ExternalOperationFailedException.class);
        }
        LOGGER.info("End to ask to update orchestrator, responseRef is {}", updateResponse);
        if (updateResponse == null) {
            LOGGER.error("to get updateResponse from orchestrator is null");
            return null;
        }
        CommonResponseRef updateResponseRef = new CommonResponseRef();
        updateResponseRef.setResult(JobStatus.Success.equals(updateResponse.getJobStatus()));
        return updateResponseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
    }
}
