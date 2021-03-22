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

package com.webank.wedatasphere.dss.appconn.visualis.publish;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPostAction;
import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.DashboardCreateResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.DisplayCreateResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.WidgetCreateResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.utils.HttpUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.ExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.SSOUrlBuilderOperationImpl;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.Ref;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class VisualisRefExportOperation implements RefExportOperation<ExportRequestRef, ResponseRef> {

    private final static Logger logger = LoggerFactory.getLogger(VisualisRefExportOperation.class);

    DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public VisualisRefExportOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        this.ssoRequestOperation = new OriginSSORequestOperation(this.developmentService.getAppDesc().getAppName());
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
            if("linkis.appjoint.visualis.widget".equalsIgnoreCase(nodeType)){
                WidgetCreateResponseRef widgetCreateResponseRef = new WidgetCreateResponseRef(externalContent);
                visualisPostAction.addRequestPayload("widgetIds", ((Double) Double.parseDouble(widgetCreateResponseRef.getWidgetId())).longValue());
            } else if("linkis.appjoint.visualis.display".equalsIgnoreCase(nodeType)){
                DisplayCreateResponseRef displayCreateResponseRef = new DisplayCreateResponseRef(externalContent);
                visualisPostAction.addRequestPayload("displayIds", ((Double) Double.parseDouble(displayCreateResponseRef.getDisplayId())).longValue());
            } else if("linkis.appjoint.visualis.dashboard".equalsIgnoreCase(nodeType)){
                DashboardCreateResponseRef dashboardCreateResponseRef = new DashboardCreateResponseRef(externalContent);
                visualisPostAction.addRequestPayload("dashboardPortalIds", ((Double) Double.parseDouble(dashboardCreateResponseRef.getDashboardPortalId())).longValue());
            } else {
                throw new ExternalOperationFailedException(90177, "Unknown task type " + requestRef.getType(), null);
            }
        } catch (Exception e) {
            logger.error("Failed to create export request", e);
        }
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
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
