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
import com.webank.wedatasphere.dss.appconn.orchestrator.ref.DefaultOrchestratorImportResponseRef;
import com.webank.wedatasphere.dss.common.protocol.ResponseImportOrchestrator;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestImportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorImportRequestRef;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorImportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author allenlliu
 * @date 2020/12/15 11:50
 */
public class OrchestratorFrameworkImportOperation implements
        RefImportOperation<OrchestratorImportRequestRef, OrchestratorImportResponseRef> {

    private DevelopmentService service;

    private final Sender sender = Sender.getSender(OrchestratorConf.ORCHESTRATOR_SERVER_PROD_NAME.getValue());

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorFrameworkImportOperation.class);


    @Override
    public OrchestratorImportResponseRef importRef(OrchestratorImportRequestRef requestRef) throws ExternalOperationFailedException {
        if (null == requestRef) {
            return null;
        }
        LOGGER.info("Begin to ask to import orchestrator, requestRef is {}", DSSCommonUtils.COMMON_GSON.toJson(requestRef));
        RequestImportOrchestrator importRequest = new RequestImportOrchestrator(requestRef.getUserName(),
                requestRef.getWorkspaceName(), requestRef.getProjectName(),
                requestRef.getProjectId(), requestRef.getResourceId(),
                requestRef.getBmlVersion(), requestRef.getOrchestratorName(), requestRef.getDSSLabels(),
                DSSCommonUtils.COMMON_GSON.toJson(requestRef.getWorkspace()));
        ResponseImportOrchestrator importResponse = null;
        try {
            importResponse = (ResponseImportOrchestrator) sender.ask(importRequest);
        } catch (final Throwable t) {
            DSSExceptionUtils.dealErrorException(60015, "import orchestrator ref failed", t,
                    ExternalOperationFailedException.class);
        }
        LOGGER.info("End to ask to import orchestrator, responseRef is {}", DSSCommonUtils.COMMON_GSON.toJson(importResponse));
        DefaultOrchestratorImportResponseRef importResponseRef = new DefaultOrchestratorImportResponseRef();
        if (null == importResponse){
            LOGGER.error("importResponse is null it means failed to import Ref");
            DSSExceptionUtils.dealErrorException(60015, "import ref response is null", ExternalOperationFailedException.class);
        }
        importResponseRef.setRefOrcId(importResponse.orcId());
        return importResponseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.service = service;
    }
}
