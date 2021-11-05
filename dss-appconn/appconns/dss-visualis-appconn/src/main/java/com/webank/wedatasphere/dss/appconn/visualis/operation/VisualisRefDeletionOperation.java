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

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisDeleteAction;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisNodeUtils;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

import java.util.Map;

public class VisualisRefDeletionOperation implements RefDeletionOperation {

    private DevelopmentService developmentService;
    private SSORequestOperation ssoRequestOperation;

    public VisualisRefDeletionOperation(DevelopmentService service) {
        this.developmentService = service;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(getAppName());
    }

    private String getAppName() {
        return VisualisAppConn.VISUALIS_APPCONN_NAME;
    }

    @Override
    public void deleteRef(RequestRef requestRef) throws ExternalOperationFailedException {
        NodeRequestRef visualisDeleteRequestRef = (NodeRequestRef) requestRef;
        if ("linkis.appconn.visualis.widget".equalsIgnoreCase(visualisDeleteRequestRef.getNodeType())) {
            deleteWidget(visualisDeleteRequestRef);
        } else if ("linkis.appconn.visualis.display".equalsIgnoreCase(visualisDeleteRequestRef.getNodeType())) {
            deleteDisplay(visualisDeleteRequestRef);
        } else if ("linkis.appconn.visualis.dashboard".equalsIgnoreCase(visualisDeleteRequestRef.getNodeType())) {
            deleteDashboardPortal(visualisDeleteRequestRef);
        } else {
            throw new ExternalOperationFailedException(90177, "Unknown task type " + visualisDeleteRequestRef.getNodeType(), null);
        }
    }

    private void deleteWidget(NodeRequestRef visualisDeleteRequestRef) throws ExternalOperationFailedException {
        String url = null;
        try {
            url = getBaseUrl() + URLUtils.widgetDeleteUrl + "/" + VisualisNodeUtils.getId(visualisDeleteRequestRef);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Delete Widget Exception", e);
        }
        VisualisDeleteAction deleteAction = new VisualisDeleteAction();
        deleteAction.setUser(visualisDeleteRequestRef.getUserName());
        SSOUrlBuilderOperation ssoUrlBuilderOperation = visualisDeleteRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(visualisDeleteRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            deleteAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, deleteAction);
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

    private void deleteDisplay(NodeRequestRef visualisDeleteRequestRef) throws ExternalOperationFailedException {
        String url = null;
        try {
            url = getBaseUrl() + URLUtils.displayUrl + "/" + VisualisNodeUtils.getId(visualisDeleteRequestRef);
        } catch (Exception e) {
            new ExternalOperationFailedException(90177, "Delete Display Exception", e);
        }
        VisualisDeleteAction deleteAction = new VisualisDeleteAction();
        deleteAction.setUser(visualisDeleteRequestRef.getUserName());
        SSOUrlBuilderOperation ssoUrlBuilderOperation = visualisDeleteRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(visualisDeleteRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            deleteAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, deleteAction);
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

    private void deleteDashboardPortal(NodeRequestRef visualisDeleteRequestRef) throws ExternalOperationFailedException {
        String url = null;
        try {
            url = getBaseUrl() + URLUtils.dashboardPortalUrl + "/" + VisualisNodeUtils.getId(visualisDeleteRequestRef);
        } catch (Exception e) {
            new ExternalOperationFailedException(90177, "Delete Dashboard Exception", e);
        }
        VisualisDeleteAction deleteAction = new VisualisDeleteAction();
        deleteAction.setUser(visualisDeleteRequestRef.getUserName());
        SSOUrlBuilderOperation ssoUrlBuilderOperation = visualisDeleteRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(visualisDeleteRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            deleteAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = (HttpResult) this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, deleteAction);
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

    private String getBaseUrl() {
        return developmentService.getAppInstance().getBaseUrl();
    }
}
