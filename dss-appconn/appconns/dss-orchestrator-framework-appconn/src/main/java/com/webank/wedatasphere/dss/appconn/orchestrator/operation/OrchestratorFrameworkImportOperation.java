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

import com.webank.wedatasphere.dss.common.protocol.ResponseImportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestImportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import org.apache.linkis.rpc.Sender;

import java.util.HashMap;
import java.util.Map;

public class OrchestratorFrameworkImportOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.ImportRequestRefImpl, RefJobContentResponseRef>
        implements RefImportOperation<ThirdlyRequestRef.ImportRequestRefImpl> {
    @Override
    public RefJobContentResponseRef importRef(ThirdlyRequestRef.ImportRequestRefImpl requestRef) {
        logger.info("Begin to ask to import orchestrator, requestRef is {}.", toJson(requestRef));
        RequestImportOrchestrator importRequest = new RequestImportOrchestrator(requestRef.getUserName(),
                requestRef.getProjectName(),
                requestRef.getProjectRefId(), (String) requestRef.getResourceMap().get(ImportRequestRef.RESOURCE_ID_KEY),
                (String) requestRef.getResourceMap().get(ImportRequestRef.RESOURCE_VERSION_KEY),
                requestRef.getName(), requestRef.getDSSLabels(),
                requestRef.getWorkspace());
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender(requestRef.getDSSLabels());
        ResponseImportOrchestrator importResponse = (ResponseImportOrchestrator) sender.ask(importRequest);
        logger.info("End to ask to import orchestrator, responseRef is {}", toJson(importResponse));
        Map<String, Object> refMap = new HashMap<>(1);
        refMap.put(OrchestratorRefConstant.ORCHESTRATOR_ID_KEY, importResponse.orcId());
        return RefJobContentResponseRef.newBuilder().setRefJobContent(refMap).success();
    }

}
