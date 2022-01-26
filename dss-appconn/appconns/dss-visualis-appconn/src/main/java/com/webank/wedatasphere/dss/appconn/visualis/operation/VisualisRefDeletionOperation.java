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

import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;


public class VisualisRefDeletionOperation implements RefDeletionOperation {

    private DevelopmentService developmentService;
    private SSORequestOperation ssoRequestOperation;

    public VisualisRefDeletionOperation(DevelopmentService service) {
        this.developmentService = service;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(VisualisAppConn.VISUALIS_APPCONN_NAME);
    }

    @Override
    public void deleteRef(RequestRef requestRef) throws ExternalOperationFailedException {
        NodeRequestRef visualisDeleteRequestRef = (NodeRequestRef) requestRef;
        String nodeType = visualisDeleteRequestRef.getNodeType().toLowerCase();
        ModuleFactory.getInstance().crateModule(nodeType).deleteRef(getBaseUrl(), visualisDeleteRequestRef, ssoRequestOperation);
    }


    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

    private String getBaseUrl() {
        return developmentService.getAppInstance().getBaseUrl();
    }
}
