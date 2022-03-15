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

import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestDeleteOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.rpc.Sender;

public class OrchestratorFrameworkDeleteOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl, ResponseRef>
        implements RefDeletionOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl> {

    @Override
    public ResponseRef deleteRef(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        logger.info("Begin to ask to delete orchestrator, requestRef is {}.", requestRef);
        Long orchestratorId = (Long) requestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATOR_ID_KEY);
        RequestDeleteOrchestrator deleteRequest = new RequestDeleteOrchestrator(requestRef.getUserName(),
                requestRef.getWorkspace().getWorkspaceName(), requestRef.getProjectName(),
                orchestratorId, requestRef.getDSSLabels());
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender(requestRef.getDSSLabels());
        ResponseOperateOrchestrator deleteResponse = (ResponseOperateOrchestrator) sender.ask(deleteRequest);
        logger.info("End to ask to delete orchestrator , responseRef is {}.", deleteResponse);
        if (! deleteResponse.isSucceed()){
            logger.error("delete response status is failed, errorMsg: {}.", deleteResponse.getMessage());
            DSSExceptionUtils.dealWarnException(60075, "failed to delete ref, errorMsg: " + deleteResponse.getMessage(),
                    ExternalOperationFailedException.class);
        }
        return ResponseRef.newInternalBuilder().success();
    }

}