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
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPostAction;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisualisRefExportOperation implements RefExportOperation<ExportRequestRef> {

    private final static Logger logger = LoggerFactory.getLogger(VisualisRefExportOperation.class);

    private DevelopmentService developmentService;
    private SSORequestOperation ssoRequestOperation;

    public VisualisRefExportOperation(DevelopmentService developmentService) {
        this.developmentService = developmentService;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(getAppName());
    }

    private String getAppName() {
        return VisualisAppConn.VISUALIS_APPCONN_NAME;
    }

    @Override
    public ResponseRef exportRef(ExportRequestRef requestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.projectUrl + "/export";
        logger.info("url:{}", url);
        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getParameter("user").toString());
        visualisPostAction.addRequestPayload("projectId", requestRef.getParameter("projectId"));
        visualisPostAction.addRequestPayload("partial", true);
        String nodeType = requestRef.getParameter("nodeType").toString().toLowerCase();
        String externalContent = null;
        try {
            externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(requestRef.getParameter("jobContent"));
        } catch (Exception e) {
            logger.error("Failed to  export request", e);
        }
        try {
            return ModuleFactory.getInstance().crateModule(nodeType).exportRef(requestRef, url, visualisPostAction, externalContent, ssoRequestOperation);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "Export Visualis Exception", e);
        }
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        developmentService = service;
    }

    private String getBaseUrl() {
        return developmentService.getAppInstance().getBaseUrl();
    }

}
