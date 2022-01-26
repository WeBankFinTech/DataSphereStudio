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

package com.webank.wedatasphere.dss.appconn.visualis.operation;

import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisOpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.server.BDPJettyServerHelper;


public class VisualisRefQueryOperation implements RefQueryOperation<OpenRequestRef> {

    private DevelopmentService developmentService;

    @Override
    public ResponseRef query(OpenRequestRef ref) throws ExternalOperationFailedException {
        VisualisOpenRequestRef visualisOpenRequestRef = (VisualisOpenRequestRef) ref;
        try {
            String externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(visualisOpenRequestRef.getJobContent());
            Long projectId = (Long) visualisOpenRequestRef.getParameter("projectId");
            String baseUrl = visualisOpenRequestRef.getParameter("redirectUrl").toString();
            String nodeType = visualisOpenRequestRef.getType().toLowerCase();
            return ModuleFactory.getInstance().crateModule(nodeType).query(visualisOpenRequestRef, externalContent, projectId, baseUrl);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Failed to parse jobContent ", e);
        }
    }


    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }
}
