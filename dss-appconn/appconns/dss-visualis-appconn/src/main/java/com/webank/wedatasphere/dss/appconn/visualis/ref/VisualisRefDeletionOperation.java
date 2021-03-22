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

package com.webank.wedatasphere.dss.appconn.visualis.ref;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisDeleteAction;
import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.VisualisDeleteRequestRef;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.SSOUrlBuilderOperationImpl;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

import java.util.Map;

public class VisualisRefDeletionOperation implements RefDeletionOperation {

    DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public VisualisRefDeletionOperation(DevelopmentService service){
        this.developmentService = service;
        this.ssoRequestOperation = new OriginSSORequestOperation(this.developmentService.getAppDesc().getAppName());
    }

    @Override
    public void deleteRef(RequestRef requestRef) throws ExternalOperationFailedException {
        VisualisDeleteRequestRef visualisDeleteRequestRef = (VisualisDeleteRequestRef) requestRef;
        if("linkis.appjoint.visualis.widget".equalsIgnoreCase(visualisDeleteRequestRef.getNodeType())){
            deleteWidget(visualisDeleteRequestRef);
        } else if("linkis.appjoint.visualis.display".equalsIgnoreCase(visualisDeleteRequestRef.getNodeType())){
            deleteDisplay(visualisDeleteRequestRef);
        } else if("linkis.appjoint.visualis.dashboard".equalsIgnoreCase(visualisDeleteRequestRef.getNodeType())){
            deleteDashboardPortal(visualisDeleteRequestRef);
        } else {
            throw new ExternalOperationFailedException(90177, "Unknown task type " + visualisDeleteRequestRef.getNodeType(), null);
        }
    }

    private void deleteWidget(VisualisDeleteRequestRef visualisDeleteRequestRef) throws ExternalOperationFailedException {
        String url = null;
        try {
            url = getBaseUrl()+ URLUtils.widgetDeleteUrl + "/" + visualisDeleteRequestRef.getId();
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Delete Widget Exception", e);
        }
        VisualisDeleteAction deleteAction = new VisualisDeleteAction();
        deleteAction.setUser(visualisDeleteRequestRef.getUsername());
        SSOUrlBuilderOperation ssoUrlBuilderOperation = visualisDeleteRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(visualisDeleteRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            deleteAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, deleteAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Delete Widget Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        if (code != 200) {
            String errorMsg = header.toString();
            throw new ExternalOperationFailedException(90177, errorMsg, null);
        }
    }

    private void deleteDisplay(VisualisDeleteRequestRef visualisDeleteRequestRef) throws ExternalOperationFailedException {
        String url = null;
        try {
            url = getBaseUrl()+ URLUtils.displayUrl + "/" + visualisDeleteRequestRef.getId();
        } catch (Exception e) {
            new ExternalOperationFailedException(90177, "Delete Display Exception", e);
        }
        VisualisDeleteAction deleteAction = new VisualisDeleteAction();
        deleteAction.setUser(visualisDeleteRequestRef.getUsername());
        SSOUrlBuilderOperation ssoUrlBuilderOperation = visualisDeleteRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(visualisDeleteRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            deleteAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, deleteAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Delete Display Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        if (code != 200) {
            String errorMsg = header.toString();
            throw new ExternalOperationFailedException(90177, errorMsg, null);
        }
    }

    private void deleteDashboardPortal(VisualisDeleteRequestRef visualisDeleteRequestRef) throws ExternalOperationFailedException {
        String url = null;
        try {
            url = getBaseUrl()+ URLUtils.dashboardPortalUrl + "/" + visualisDeleteRequestRef.getId();
        } catch (Exception e) {
            new ExternalOperationFailedException(90177, "Delete Dashboard Exception", e);
        }
        VisualisDeleteAction deleteAction = new VisualisDeleteAction();
        deleteAction.setUser(visualisDeleteRequestRef.getUsername());
        SSOUrlBuilderOperation ssoUrlBuilderOperation = visualisDeleteRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(visualisDeleteRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            deleteAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, deleteAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Delete Display Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        if (code != 200) {
            String errorMsg = header.toString();
            throw new ExternalOperationFailedException(90177, errorMsg, null);
        }
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl();
    }
}
