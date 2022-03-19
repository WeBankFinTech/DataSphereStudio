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

import com.webank.wedatasphere.dss.common.protocol.ResponseExportOrchestrator;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestExportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import org.apache.linkis.rpc.Sender;

import java.util.HashMap;
import java.util.Map;

public class OrchestratorFrameworkExportOperation
        extends AbstractDevelopmentOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl, ExportResponseRef>
        implements RefExportOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl> {

    private final Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender();

    @Override
    public ExportResponseRef exportRef(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef) {
        logger.info("Begin to ask to export orchestrator, requestRef is {}.", DSSCommonUtils.COMMON_GSON.toJson(requestRef));
        Long orchestratorId = (Long) requestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATOR_ID_KEY);
        Long orchestratorVersionId = (Long) requestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATOR_VERSION_ID_KEY);
        boolean addOrcVersionFlag = false;
        if(requestRef.getRefJobContent().containsKey(OrchestratorRefConstant.ORCHESTRATOR_ADD_VERSION_FLAG_KEY)) {
            addOrcVersionFlag = (boolean) requestRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATOR_ADD_VERSION_FLAG_KEY);
        }
        RequestExportOrchestrator exportRequest = new RequestExportOrchestrator(requestRef.getUserName(),
                orchestratorId, orchestratorVersionId,
                requestRef.getProjectName(), requestRef.getDSSLabels(), addOrcVersionFlag,
                requestRef.getWorkspace());
        ResponseExportOrchestrator exportResponse = (ResponseExportOrchestrator) sender.ask(exportRequest);
        logger.info("End to ask to export orchestrator, responseRef is {}.", DSSCommonUtils.COMMON_GSON.toJson(exportResponse));
        Map<String, Object> resourceMap = new HashMap<>(2);
        resourceMap.put(ImportRequestRef.RESOURCE_ID_KEY, exportResponse.resourceId());
        resourceMap.put(ImportRequestRef.RESOURCE_VERSION_KEY, exportResponse.version());
        resourceMap.put(OrchestratorRefConstant.ORCHESTRATOR_VERSION_ID_KEY, exportResponse.orcVersionId());
        return ExportResponseRef.newBuilder().setResourceMap(resourceMap).success();
    }

}
