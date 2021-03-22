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
import com.webank.wedatasphere.dss.appconn.orchestrator.ref.DefaultOrchestratorExportResponseRef;
import com.webank.wedatasphere.dss.common.protocol.ResponseExportOrchestrator;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestExportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorExportRequestRef;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author allenlliu
 * @date 2020/12/15 11:48
 */
public class OrchestratorFrameworkExportOperation implements RefExportOperation<OrchestratorExportRequestRef, OrchestratorExportResponseRef> {

    private  DevelopmentService service;

    private final Sender sender = Sender.getSender(OrchestratorConf.ORCHESTRATOR_SERVER_DEV_NAME.getValue());

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorFrameworkExportOperation.class);


    @Override
    public OrchestratorExportResponseRef exportRef(OrchestratorExportRequestRef requestRef) throws ExternalOperationFailedException {
        if (null == requestRef){
            LOGGER.error("request ref for exporting is null, it is a fatal error");
            return null;
        }
        LOGGER.info("Begin to ask to export orchestrator, requestRef is {}", DSSCommonUtils.COMMON_GSON.toJson(requestRef));
        RequestExportOrchestrator exportRequest = new RequestExportOrchestrator(requestRef.getUserName(),
                requestRef.getWorkspaceName(), requestRef.getOrchestratorId(), -1L,
                requestRef.getProjectName(), requestRef.getDSSLabels(), requestRef.getAddOrcVersionFlag(),
                BDPJettyServerHelper.gson().toJson(requestRef.getWorkspace()));
        ResponseExportOrchestrator exportResponse = null;
        try{
            exportResponse = (ResponseExportOrchestrator) sender.ask(exportRequest);
        }catch(final Throwable e){
            DSSExceptionUtils.dealErrorException(60015, "export orchestrator ref failed", e,
                    ExternalOperationFailedException.class);
        }
        LOGGER.info("End to ask to export orchestrator, responseRef is {}", DSSCommonUtils.COMMON_GSON.toJson(exportResponse));
        if(exportResponse == null){
            LOGGER.error("exportResponse is null, it means export is failed");
            DSSExceptionUtils.dealErrorException(63323, "exportResponse is null, it means export is failed", ExternalOperationFailedException.class);
        }
        DefaultOrchestratorExportResponseRef exportResponseRef = new DefaultOrchestratorExportResponseRef();
        exportResponseRef.setBmlVersion(exportResponse.version());
        exportResponseRef.setResourceId(exportResponse.resourceId());
        exportResponseRef.setOrchestratorVersionId(exportResponse.orcVersionId());
        return exportResponseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.service = service;
    }
}
