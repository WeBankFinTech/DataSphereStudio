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
import com.webank.wedatasphere.dss.appconn.visualis.publish.VisualisExportResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisCommonResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisualisRefExportOperation implements RefExportOperation<ExportRequestRef> {

    private final static Logger logger = LoggerFactory.getLogger(VisualisRefExportOperation.class);

    DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public VisualisRefExportOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(getAppName());
    }

    private String getAppName() {
        return VisualisAppConn.VISUALIS_APPCONN_NAME;
    }

    @Override
    public ResponseRef exportRef(ExportRequestRef requestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.projectUrl + "/export";
        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getParameter("user").toString());
        visualisPostAction.addRequestPayload("projectId", requestRef.getParameter("projectId"));
        visualisPostAction.addRequestPayload("partial", true);
        String nodeType = requestRef.getParameter("nodeType").toString();
        String externalContent = null;
        try {
            externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(requestRef.getParameter("jobContent"));
            if("linkis.appconn.visualis.widget".equalsIgnoreCase(nodeType)){
                VisualisCommonResponseRef widgetCreateResponseRef = new VisualisCommonResponseRef(externalContent);
                visualisPostAction.addRequestPayload("widgetIds", ((Double) Double.parseDouble(widgetCreateResponseRef.getWidgetId())).longValue());
            } else if("linkis.appconn.visualis.display".equalsIgnoreCase(nodeType)){
                VisualisCommonResponseRef displayCreateResponseRef = new VisualisCommonResponseRef(externalContent);
                visualisPostAction.addRequestPayload("displayIds", ((Double) Double.parseDouble(displayCreateResponseRef.getDisplayId())).longValue());
            } else if("linkis.appconn.visualis.dashboard".equalsIgnoreCase(nodeType)){
                VisualisCommonResponseRef dashboardCreateResponseRef = new VisualisCommonResponseRef(externalContent);
                visualisPostAction.addRequestPayload("dashboardPortalIds", ((Double) Double.parseDouble(dashboardCreateResponseRef.getDashboardId())).longValue());
            } else {
                throw new ExternalOperationFailedException(90177, "Unknown task type " + requestRef.getType(), null);
            }
        } catch (Exception e) {
            logger.error("Failed to create export request", e);
        }
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        ResponseRef responseRef;
        try{
            visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
            responseRef = new VisualisExportResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(90176, "Export Visualis Exception", e);
        }
        return responseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        developmentService = service;
    }

    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl();
    }

}
