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

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestUpdateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.OnlyDevelopmentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.rpc.Sender;

public class OrchestratorFrameworkUpdateOperation
        extends AbstractDevelopmentOperation<OnlyDevelopmentRequestRef.UpdateRequestRefImpl, ResponseRef>
        implements RefUpdateOperation<OnlyDevelopmentRequestRef.UpdateRequestRefImpl> {

    @Override
    public ResponseRef updateRef(OnlyDevelopmentRequestRef.UpdateRequestRefImpl requestRef) throws ExternalOperationFailedException {
        logger.info("Begin to ask to update orchestrator, requestRef is {}.", toJson(requestRef));
        DSSOrchestratorInfo dssOrchestratorInfo = (DSSOrchestratorInfo) requestRef.getRefJobContent().get(OrchestratorRefConstant.DSS_ORCHESTRATOR_INFO_KEY);
        RequestUpdateOrchestrator updateRequest = new RequestUpdateOrchestrator(requestRef.getUserName(),
                requestRef.getWorkspace().getWorkspaceName(), dssOrchestratorInfo, requestRef.getDSSLabels());
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender(requestRef.getDSSLabels());
        ResponseOperateOrchestrator updateResponse = (ResponseOperateOrchestrator) sender.ask(updateRequest);
        logger.info("End to ask to update orchestrator, responseRef is {} with message {}.", updateResponse.getJobStatus(), updateResponse.getMessage());
        if(updateResponse.isSucceed()) {
            return ResponseRef.newInternalBuilder().success();
        } else {
            return ResponseRef.newInternalBuilder().error(updateResponse.getMessage());
        }
    }
}
