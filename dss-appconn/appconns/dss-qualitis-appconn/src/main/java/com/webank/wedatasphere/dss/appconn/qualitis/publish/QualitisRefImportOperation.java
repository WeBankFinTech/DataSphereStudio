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

package com.webank.wedatasphere.dss.appconn.qualitis.publish;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.appconn.qualitis.QualitisAppConn;
import com.webank.wedatasphere.dss.appconn.qualitis.model.QualitisPutAction;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.entity.QualitisImportRequestRef;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.entity.QualitisImportResponseRef;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.bml.client.BmlClient;
import com.webank.wedatasphere.linkis.bml.client.BmlClientFactory;
import com.webank.wedatasphere.linkis.bml.protocol.BmlDownloadResponse;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QualitisRefImportOperation implements RefImportOperation<ImportRequestRef> {

    private final static Logger logger = LoggerFactory.getLogger(QualitisRefImportOperation.class);

    DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;
    private static final String IMPORT_RULE_URL = "/qualitis/outer/api/v1/projector/rule/import";

    private String appId = "linkis_id";
    private String appToken = "a33693de51";

    /**
     * Default user for BML download and upload.
     */
    private static final String DEFAULT_USER = "hadoop";

    public QualitisRefImportOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        this.ssoRequestOperation = new OriginSSORequestOperation(QualitisAppConn.QUALITIS_APPCONN_NAME);
    }

    @Override
    public ResponseRef importRef(ImportRequestRef requestRef) throws ExternalOperationFailedException {
        QualitisImportResponseRef qualitisImportResponseRef = new QualitisImportResponseRef();
        QualitisImportRequestRef qualitisImportRequestRef = (QualitisImportRequestRef) requestRef;
        Map resourceMap = qualitisImportRequestRef.getResourceMap();
        Map<String, Object> ruleGroupId = new HashMap<>(1);
        /**
         * BML client download opeartion.
         */
        BmlClient bmlClient = BmlClientFactory.createBmlClient(DEFAULT_USER);
        BmlDownloadResponse bmlDownloadResponse = bmlClient.downloadResource(DEFAULT_USER, resourceMap.get("resourceId").toString(), resourceMap.get("version").toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String dataJsonString = null;
        try {
            dataJsonString = IOUtils.toString(bmlDownloadResponse.inputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> data = null;
        try {
            data = objectMapper.readValue(dataJsonString, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        logger.info("BML downloaded data: ", data.toString());
        String url = null;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), IMPORT_RULE_URL, appId, appToken, RandomStringUtils
                    .randomNumeric(5)
                , String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error! Can not export rule group, rule group id: {}", ruleGroupId, e);
            throw new ExternalOperationFailedException(90156, "Error when building export url.", e);
        }
        /**
         * Put new project ID of produce center.
         */
        QualitisPutAction qualitisPutAction = new QualitisPutAction();
        qualitisPutAction.addRequestPayload("new_project_id", requestRef.getParameter("projectId"));

        SSOUrlBuilderOperation ssoUrlBuilderOperation = qualitisImportRequestRef.getSsoUrlBuilderOperation();
        ssoUrlBuilderOperation.setAppName(QualitisAppConn.QUALITIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);

        ssoUrlBuilderOperation.setWorkspace(qualitisImportRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            qualitisPutAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, qualitisPutAction);
            response = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
        } catch (Exception e) {
            logger.error("Kill Job Exception", e);
            throw new ExternalOperationFailedException(90156, "Error when request export url.", e);
        }
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        String errorMsg = "";
        if (code != 200) {
            errorMsg = header.toString();
            logger.error(errorMsg);
        }
        qualitisImportResponseRef.setRuleGroupId((Map<String, Object>)  resMap.get("data"));

        return qualitisImportResponseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl();
    }

}
