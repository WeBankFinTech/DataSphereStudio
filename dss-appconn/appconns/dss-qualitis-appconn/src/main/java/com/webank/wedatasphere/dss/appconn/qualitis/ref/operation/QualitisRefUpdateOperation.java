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

package com.webank.wedatasphere.dss.appconn.qualitis.ref.operation;

import com.webank.wedatasphere.dss.appconn.qualitis.QualitisAppConn;
import com.webank.wedatasphere.dss.appconn.qualitis.model.QualitisPostAction;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CommonResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.UpdateRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QualitisRefUpdateOperation implements RefUpdateOperation<UpdateRequestRef> {

    private static final String UPDATE_RULE_PATH = "/qualitis/outer/api/v1/projector/rule/modify";

    DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    private String appId = "linkis_id";
    private String appToken = "a33693de51";

    private final static Logger logger = LoggerFactory.getLogger(QualitisRefUpdateOperation.class);
    public QualitisRefUpdateOperation(DevelopmentService developmentService) {
        this.developmentService = developmentService;
        this.ssoRequestOperation = new OriginSSORequestOperation(QualitisAppConn.QUALITIS_APPCONN_NAME);
    }

    @Override
    public ResponseRef updateRef(UpdateRequestRef requestRef) throws ExternalOperationFailedException {

        if (requestRef instanceof NodeRequestRef){
            return new CommonResponseRef();
        } else {
            return updateQualitisCS((NodeRequestRef) requestRef);
        }

    }

    private ResponseRef updateQualitisCS(NodeRequestRef qualitisUpdateCSRequestRef) throws ExternalOperationFailedException {
        String csId = qualitisUpdateCSRequestRef.getContextID();
        String projectName = qualitisUpdateCSRequestRef.getProjectName();
        String url = null;

        try {
            url = HttpUtils.buildUrI(getBaseUrl(), UPDATE_RULE_PATH, appId, appToken, RandomStringUtils.randomNumeric(5),
                String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to build update context service url.", e);
            throw new ExternalOperationFailedException(90176, "Failed to build update context service url.", e);
        }
        QualitisPostAction qualitisPostAction = new QualitisPostAction();
        qualitisPostAction.addRequestPayload("cs_id", csId);
        qualitisPostAction.addRequestPayload("project_name", projectName);

        SSOUrlBuilderOperation ssoUrlBuilderOperation = qualitisUpdateCSRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(QualitisAppConn.QUALITIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);

        ssoUrlBuilderOperation.setWorkspace(qualitisUpdateCSRequestRef.getWorkspace().getWorkspaceName());
        Map<String, Object> resMap;
        HttpResult httpResult;
        String response = "";

        try {
            qualitisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, qualitisPostAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            logger.error("Failed to request update context service url.", e);
            throw new ExternalOperationFailedException(90176, "Failed to request update context service url.", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        String errorMsg = "";
        if (code != 200) {
            errorMsg = header.toString();
            throw new ExternalOperationFailedException(90176, errorMsg, null);
        }
        return new CommonResponseRef(response, code);
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl();
    }
}
