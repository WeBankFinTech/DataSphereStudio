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
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestDeleteOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorDeleteRequestRef;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrchestratorFrameworkDeleteOperation implements
        RefDeletionOperation<OrchestratorDeleteRequestRef> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorFrameworkDeleteOperation.class);

    @Override
    public void setDevelopmentService(DevelopmentService service) {
    }

    @Override
    public void deleteRef(OrchestratorDeleteRequestRef requestRef) throws ExternalOperationFailedException {
        LOGGER.info("Begin to ask to delete orchestrator, requestRef is {}", requestRef);
        RequestDeleteOrchestrator deleteRequest = new RequestDeleteOrchestrator(requestRef.getUserName(),
                requestRef.getWorkspaceName(), requestRef.getProjectName(),
                requestRef.getOrcId(), requestRef.getDSSLabels());
        ResponseOperateOrchestrator deleteResponse = null;
        try {
            Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender(requestRef.getDSSLabels());
            deleteResponse = (ResponseOperateOrchestrator) sender.ask(deleteRequest);
            LOGGER.info("End to ask to delete orchestrator , responseRef is {}", deleteResponse);
            if (deleteResponse == null || !deleteResponse.getJobStatus().equals(JobStatus.Success)){
                LOGGER.error("delete response is null or delete response status is not success");
                DSSExceptionUtils.dealErrorException(60075, "failed to delete ref", ExternalOperationFailedException.class);
            }
        } catch (Exception e) {
            DSSExceptionUtils.dealErrorException(60015, "delete orchestrator ref failed", ExternalOperationFailedException.class);
        }
    }

}