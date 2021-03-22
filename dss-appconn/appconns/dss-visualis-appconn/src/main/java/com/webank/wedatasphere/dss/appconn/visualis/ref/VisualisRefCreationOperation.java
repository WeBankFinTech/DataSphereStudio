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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPostAction;
import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.*;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.CreateRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.SSOUrlBuilderOperationImpl;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.cs.common.utils.CSCommonUtils;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

public class VisualisRefCreationOperation implements RefCreationOperation<CreateRequestRef, ResponseRef> {

    DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public VisualisRefCreationOperation(DevelopmentService service){
        this.developmentService = service;
        this.ssoRequestOperation = new OriginSSORequestOperation(this.developmentService.getAppDesc().getAppName());
    }

    @Override
    public ResponseRef createRef(CreateRequestRef requestRef) throws ExternalOperationFailedException {
        VisualisCreateRequestRef visualisCreateRequestRef = (VisualisCreateRequestRef) requestRef;
        requestRef.setParameter("projectId", visualisCreateRequestRef.getProjectId());
        if("linkis.appjoint.visualis.widget".equalsIgnoreCase(visualisCreateRequestRef.getNodeType())){
            return sendWidgetRequest(visualisCreateRequestRef);
        } else if("linkis.appjoint.visualis.display".equalsIgnoreCase(visualisCreateRequestRef.getNodeType())){
            return sendDisplayRequest(visualisCreateRequestRef);
        }else if("linkis.appjoint.visualis.dashboard".equalsIgnoreCase(visualisCreateRequestRef.getNodeType())){
            return sendDashboardRequest(visualisCreateRequestRef);
        } else {
            throw new ExternalOperationFailedException(90177, "Unknown task type " + visualisCreateRequestRef.getNodeType(), null);
        }
    }

    private ResponseRef sendWidgetRequest(VisualisCreateRequestRef requestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.widgetUrl;
        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getUsername());
        visualisPostAction.addRequestPayload("widgetName", requestRef.getName());
        visualisPostAction.addRequestPayload("projectId", requestRef.getParameter("projectId"));
        visualisPostAction.addRequestPayload(CSCommonUtils.CONTEXT_ID_STR, requestRef.getJobContent().get(CSCommonUtils.CONTEXT_ID_STR));
        if(requestRef.getJobContent().get("bindViewKey") != null){
            String viewNodeName = requestRef.getJobContent().get("bindViewKey").toString();
            if(StringUtils.isNotBlank(viewNodeName) && !"empty".equals(viewNodeName)){
                viewNodeName = getNodeNameByKey(viewNodeName,(String) requestRef.getJobContent().get("json"));
                visualisPostAction.addRequestPayload(CSCommonUtils.NODE_NAME_STR, viewNodeName);
            }
        }
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        ResponseRef responseRef;
        try{
            visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            //
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
            responseRef = new WidgetCreateResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(90177, "Create Widget Exception", e);
        }
        // cs
        VisualisRefUpdateOperation visualisRefUpdateOperation = new VisualisRefUpdateOperation(developmentService);
        VisualisUpdateCSRequestRef visualisUpdateCSRequestRef = new VisualisUpdateCSRequestRef();
        visualisUpdateCSRequestRef.setContextID((String) requestRef.getJobContent().get(CSCommonUtils.CONTEXT_ID_STR));
        visualisUpdateCSRequestRef.setJobContent(responseRef.toMap());
        visualisUpdateCSRequestRef.setUserName(requestRef.getUsername());
        visualisUpdateCSRequestRef.setNodeType(requestRef.getNodeType());
        visualisUpdateCSRequestRef.setWorkspace(requestRef.getWorkspace());
        visualisRefUpdateOperation.updateRef(visualisUpdateCSRequestRef);
        return responseRef;
    }

    public static String getNodeNameByKey(String key, String json) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        JsonArray nodeJsonArray = jsonObject.getAsJsonArray("nodes");
        List<DSSNode> dwsNodes = DSSCommonUtils.COMMON_GSON.fromJson(nodeJsonArray, new TypeToken<List<DSSNodeDefault>>() {
        }.getType());
        return dwsNodes.stream().filter(n -> key.equals(n.getId())).map(DSSNode::getName).findFirst().orElse("");
    }


    private ResponseRef sendDisplayRequest(VisualisCreateRequestRef requestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.displayUrl;
        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getUsername());
        visualisPostAction.addRequestPayload("name", requestRef.getName());
        visualisPostAction.addRequestPayload("projectId", requestRef.getParameter("projectId"));
        visualisPostAction.addRequestPayload("avatar", "18");
        visualisPostAction.addRequestPayload("publish", true);
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        DisplayCreateResponseRef responseRef;
        try{
            visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
            responseRef = new DisplayCreateResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(90177, "Create Display Exception", e);
        }
        createDisplaySlide(responseRef, requestRef);
        return responseRef;
    }

    private ResponseRef sendDashboardRequest(VisualisCreateRequestRef requestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.dashboardPortalUrl;
        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getUsername());
        visualisPostAction.addRequestPayload("name", requestRef.getName());
        visualisPostAction.addRequestPayload("projectId", requestRef.getParameter("projectId"));
        visualisPostAction.addRequestPayload("avatar", "18");
        visualisPostAction.addRequestPayload("publish", true);
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        DashboardCreateResponseRef responseRef;
        try{
            visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
            responseRef = new DashboardCreateResponseRef(httpResult.getResponseBody());
        } catch (Exception e){
            throw new ExternalOperationFailedException(90177, "Create Dashboard Exception", e);
        }
        createDashboard(responseRef, requestRef);
        return responseRef;
    }

    private void createDisplaySlide(DisplayCreateResponseRef displayCreateResponseRef, VisualisCreateRequestRef requestRef) throws ExternalOperationFailedException{
        String url = getBaseUrl() + URLUtils.displayUrl + "/" + displayCreateResponseRef.getDisplayId() + "/slides";
        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getUsername());
        visualisPostAction.addRequestPayload("config", URLUtils.displaySlideConfig);
        visualisPostAction.addRequestPayload("displayId", Long.parseLong(displayCreateResponseRef.getDisplayId()));
        visualisPostAction.addRequestPayload("index", 0);
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        Map<String, Object> resMap = Maps.newHashMap();
        try{
            visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
            resMap = BDPJettyServerHelper.jacksonJson().readValue(httpResult.getResponseBody(), Map.class);
        } catch (Exception e){
            throw new ExternalOperationFailedException(90177, "Create DisplaySlide Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        if (code != 200) {
            String errorMsg = header.toString();
            throw new ExternalOperationFailedException(90176, errorMsg, null);
        }
    }

    private void createDashboard(DashboardCreateResponseRef dashboardCreateResponseRef, VisualisCreateRequestRef requestRef) throws ExternalOperationFailedException{
        String url = getBaseUrl() + URLUtils.dashboardPortalUrl + "/" + dashboardCreateResponseRef.getDashboardPortalId() + "/dashboards";
        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getUsername());
        visualisPostAction.addRequestPayload("config", "");
        visualisPostAction.addRequestPayload("dashboardPortalId", Long.parseLong(dashboardCreateResponseRef.getDashboardPortalId()));
        visualisPostAction.addRequestPayload("index", 0);
        visualisPostAction.addRequestPayload("name", requestRef.getName());
        visualisPostAction.addRequestPayload("parentId", 0);
        visualisPostAction.addRequestPayload("type", 1);
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        Map<String, Object> resMap = Maps.newHashMap();
        try{
            visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
            resMap = BDPJettyServerHelper.jacksonJson().readValue(httpResult.getResponseBody(), Map.class);
        } catch (Exception e){
            throw new ExternalOperationFailedException(90177, "Create Dashboard Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        if (code != 200) {
            String errorMsg = header.toString();
            throw new ExternalOperationFailedException(90176, errorMsg, null);
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
