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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisDeleteAction;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPostAction;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPutAction;
import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.VisualisDeleteRequestRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.VisualisUpdateCSRequestRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.VisualisUpdateRequestRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.VisualisUpdateResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.crud.UpdateRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.cs.common.utils.CSCommonUtils;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

import java.util.Map;

public class VisualisRefUpdateOperation implements RefUpdateOperation<UpdateRequestRef, ResponseRef> {

    DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public VisualisRefUpdateOperation(DevelopmentService developmentService) {
        this.developmentService = developmentService;
        this.ssoRequestOperation = new OriginSSORequestOperation(this.developmentService.getAppDesc().getAppName());
    }

    @Override
    public ResponseRef updateRef(UpdateRequestRef requestRef) throws ExternalOperationFailedException {
        if(requestRef instanceof VisualisUpdateRequestRef){
            VisualisUpdateRequestRef visualisUpdateRequestRef = (VisualisUpdateRequestRef) requestRef;
            if("linkis.appjoint.visualis.widget".equalsIgnoreCase(visualisUpdateRequestRef.getNodeType())){
                return updateWidget(visualisUpdateRequestRef);
            } else if("linkis.appjoint.visualis.display".equalsIgnoreCase(visualisUpdateRequestRef.getNodeType())){
                return updateDisplay(visualisUpdateRequestRef);
            } else if("linkis.appjoint.visualis.dashboard".equalsIgnoreCase(visualisUpdateRequestRef.getNodeType())){
                return updateDashboardPortal(visualisUpdateRequestRef);
            } else {
                throw new ExternalOperationFailedException(90177, "Unknown task type " + visualisUpdateRequestRef.getNodeType(), null);
            }
        } else {
            VisualisUpdateCSRequestRef visualisUpdateCSRequestRef = (VisualisUpdateCSRequestRef) requestRef;
            if("linkis.appjoint.visualis.widget".equalsIgnoreCase(visualisUpdateCSRequestRef.getNodeType())){
                return updateWidgetCS(visualisUpdateCSRequestRef);
            } else {
                throw new ExternalOperationFailedException(90177, "Unknown task type " + visualisUpdateCSRequestRef.getNodeType(), null);
            }
        }

    }

    private ResponseRef updateWidgetCS(VisualisUpdateCSRequestRef visualisUpdateCSRequestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.widgetContextUrl;
        VisualisPostAction postAction = new VisualisPostAction();
        try {
            postAction.addRequestPayload("id", Integer.parseInt(visualisUpdateCSRequestRef.getId()));
            postAction.addRequestPayload(CSCommonUtils.CONTEXT_ID_STR, visualisUpdateCSRequestRef.getContextID());
            postAction.setUser(visualisUpdateCSRequestRef.getUsername());
            SSOUrlBuilderOperation ssoUrlBuilderOperation = visualisUpdateCSRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
            ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
            ssoUrlBuilderOperation.setReqUrl(url);
            ssoUrlBuilderOperation.setWorkspace(visualisUpdateCSRequestRef.getWorkspace().getWorkspaceName());
            postAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, postAction);
            String response = httpResult.getResponseBody();
            Map<String, Object> resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
            int status = (int) resMap.get("status");
            if (status != 0) {
                String errorMsg = resMap.get("message").toString();
                throw new ExternalOperationFailedException(90177, errorMsg);
            }
            return new VisualisUpdateResponseRef();
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Update CS Exception", e);
        }
    }

    private ResponseRef updateDashboardPortal(VisualisUpdateRequestRef visualisUpdateRequestRef) throws ExternalOperationFailedException {
        String url = null;
        String id = null;
        try {
            id  = visualisUpdateRequestRef.getId();
            url = getBaseUrl()+ URLUtils.dashboardPortalUrl + "/" + id;
        } catch (Exception e) {
            new ExternalOperationFailedException(90177, "Update Dashboard Exception", e);
        }
        VisualisPutAction putAction = new VisualisPutAction();
        putAction.addRequestPayload("projectId", visualisUpdateRequestRef.getProjectId());
        putAction.addRequestPayload("name", visualisUpdateRequestRef.getName());
        putAction.addRequestPayload("id", Long.parseLong(id));
        putAction.addRequestPayload("avatar", "9");
        putAction.addRequestPayload("description", "");
        putAction.addRequestPayload("publish", true);
        putAction.addRequestPayload("roleIds", Lists.newArrayList());
        putAction.setUser(visualisUpdateRequestRef.getUsername());
        SSOUrlBuilderOperation ssoUrlBuilderOperation = visualisUpdateRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(visualisUpdateRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            putAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, putAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Update Dashboard Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        if (code != 200) {
            String errorMsg = header.toString();
            throw new ExternalOperationFailedException(90177, errorMsg, null);
        }
        return new VisualisUpdateResponseRef();
    }

    private ResponseRef updateDisplay(VisualisUpdateRequestRef visualisUpdateRequestRef) throws ExternalOperationFailedException {
        String url = null;
        String id = null;
        try {
            id = visualisUpdateRequestRef.getId();
            url = getBaseUrl()+ URLUtils.displayUrl + "/" + id;
        } catch (Exception e) {
            new ExternalOperationFailedException(90177, "Update Display Exception", e);
        }

        VisualisPutAction putAction = new VisualisPutAction();
        putAction.addRequestPayload("projectId", visualisUpdateRequestRef.getProjectId());
        putAction.addRequestPayload("name", visualisUpdateRequestRef.getName());
        putAction.addRequestPayload("id", Long.parseLong(id));
        putAction.addRequestPayload("avatar", "9");
        putAction.addRequestPayload("description", "");
        putAction.addRequestPayload("publish", true);
        putAction.addRequestPayload("roleIds", Lists.newArrayList());
        putAction.setUser(visualisUpdateRequestRef.getUsername());
        SSOUrlBuilderOperation ssoUrlBuilderOperation = visualisUpdateRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(visualisUpdateRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            putAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, putAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Update Display Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        if (code != 200) {
            String errorMsg = header.toString();
            throw new ExternalOperationFailedException(90177, errorMsg, null);
        }
        return new VisualisUpdateResponseRef();
    }

    private ResponseRef updateWidget(VisualisUpdateRequestRef visualisUpdateRequestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl()+ URLUtils.widgetUpdateUrl;
        VisualisPostAction postAction = new VisualisPostAction();
        try {
            postAction.addRequestPayload("id",  Long.parseLong(visualisUpdateRequestRef.getId()));
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Update Widget Exception", e);
        }
        postAction.addRequestPayload("name", visualisUpdateRequestRef.getName());
        postAction.setUser(visualisUpdateRequestRef.getUsername());
        SSOUrlBuilderOperation ssoUrlBuilderOperation = visualisUpdateRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(visualisUpdateRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            postAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, postAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Update Widget Exception", e);
        }
        int status = (int) resMap.get("status");
        if (status != 0) {
            String errorMsg = resMap.get("message").toString();
            throw new ExternalOperationFailedException(90177, errorMsg);
        }
        return new VisualisUpdateResponseRef();
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl();
    }
}
