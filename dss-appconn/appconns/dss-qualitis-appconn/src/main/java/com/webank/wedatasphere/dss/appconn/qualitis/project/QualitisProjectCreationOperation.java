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

package com.webank.wedatasphere.dss.appconn.qualitis.project;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.appconn.qualitis.QualitisAppConn;
import com.webank.wedatasphere.dss.appconn.qualitis.model.QualitisPostAction;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QualitisProjectCreationOperation implements ProjectCreationOperation {

    private static Logger logger = LoggerFactory.getLogger(QualitisProjectCreationOperation.class);
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;
    StructureService structureService;

    private static final String CREATE_PROJECT_PATH = "/qualitis/outer/api/v1/project/workflow";

    private String appId = "linkis_id";
    private String appToken = "a33693de51";

    public QualitisProjectCreationOperation(StructureService service, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) {
        this.structureService = service;
        this.ssoRequestOperation = ssoRequestOperation;
    }

    @Override
    public ProjectResponseRef createProject(ProjectRequestRef projectRef) throws ExternalOperationFailedException {
        String url = null;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), CREATE_PROJECT_PATH, appId, appToken, RandomStringUtils.randomNumeric(5), String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Create Qualitis Project Exception", e);
            throw new ExternalOperationFailedException(90176, "Create qualitis project by build url exception", e);
        }
        QualitisPostAction qualitisPostAction = new QualitisPostAction();
        qualitisPostAction.setUser(projectRef.getCreateBy());

        qualitisPostAction.addRequestPayload("project_name", projectRef.getName());
        qualitisPostAction.addRequestPayload("description", projectRef.getDescription());
        qualitisPostAction.addRequestPayload("username", projectRef.getCreateBy());

        SSOUrlBuilderOperation ssoUrlBuilderOperation = projectRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(QualitisAppConn.QUALITIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);

        ssoUrlBuilderOperation.setWorkspace(projectRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            qualitisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, qualitisPostAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            logger.error("Create Qualitis Project Exception", e);
            throw new ExternalOperationFailedException(90176, "Create qualitis project exception", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        String errorMsg = "";
        if (code != 200) {
            errorMsg = header.toString();
            throw new ExternalOperationFailedException(90176, errorMsg, null);
        }
        Integer projectId = (Integer) ((Map<String, Object>) resMap.get("payload")).get("id");
        QualitisProjectResponseRef qualitisProjectResponseRef = null;
        try {
            qualitisProjectResponseRef = new QualitisProjectResponseRef(response, code);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "Failed to parse response json", e);
        }
        qualitisProjectResponseRef.setAppInstance(structureService.getAppInstance());
        qualitisProjectResponseRef.setProjectRefId(projectId.longValue());
        qualitisProjectResponseRef.setErrorMsg(errorMsg);
        return qualitisProjectResponseRef;
    }

    @Override
    public void init() {

    }

    @Override
    public void setStructureService(StructureService service) {
        this.structureService = service;
    }

    private String getBaseUrl(){
        return structureService.getAppInstance().getBaseUrl();
    }
}
