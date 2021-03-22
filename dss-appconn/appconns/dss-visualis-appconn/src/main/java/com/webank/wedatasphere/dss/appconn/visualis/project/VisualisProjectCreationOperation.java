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

package com.webank.wedatasphere.dss.appconn.visualis.project;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPostAction;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.SSOUrlBuilderOperationImpl;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import com.webank.wedatasphere.linkis.server.conf.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class VisualisProjectCreationOperation implements ProjectCreationOperation {

    private static Logger logger = LoggerFactory.getLogger(VisualisProjectCreationOperation.class);
    private final static String projectUrl = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/projects";
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;
    StructureService structureService;

    public VisualisProjectCreationOperation(StructureService service) {
        this.structureService = service;
        this.ssoRequestOperation = new OriginSSORequestOperation(this.structureService.getAppDesc().getAppName());
    }

    @Override
    public ProjectResponseRef createProject(ProjectRequestRef projectRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + projectUrl;
        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(projectRef.getCreateBy());
        visualisPostAction.addRequestPayload("name", projectRef.getName());
        visualisPostAction.addRequestPayload("description", projectRef.getDescription());
        visualisPostAction.addRequestPayload("pic", "6");
        visualisPostAction.addRequestPayload("visibility", true);
        SSOUrlBuilderOperation ssoUrlBuilderOperation = projectRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(structureService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(projectRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            logger.error("Create Visualis Project Exception", e);
            throw new ExternalOperationFailedException(90176, "Create Visualis Project Exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        String errorMsg = "";
        if (code != 200) {
            errorMsg = header.toString();
            throw new ExternalOperationFailedException(90176, errorMsg, null);
        }
        Integer projectId = (Integer) ((Map<String, Object>) resMap.get("payload")).get("id");
        VisualisProjectResponseRef visualisProjectResponseRef = null;
        try {
            visualisProjectResponseRef = new VisualisProjectResponseRef(response, code);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "failed to parse response json", e);
        }
        visualisProjectResponseRef.setAppInstance(structureService.getAppInstance());
        visualisProjectResponseRef.setProjectRefId(projectId.longValue());
        visualisProjectResponseRef.setErrorMsg(errorMsg);
        return visualisProjectResponseRef;
    }

    @Override
    public void setStructureService(StructureService service) {
        this.structureService = service;
    }

    private String getBaseUrl(){
        return structureService.getAppInstance().getBaseUrl();
    }
}
