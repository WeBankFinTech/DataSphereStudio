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
import com.webank.wedatasphere.dss.appconn.qualitis.model.QualitisGetAction;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.entity.QualitisExportRequestRef;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.entity.QualitisExportResponseRef;
import com.webank.wedatasphere.dss.appconn.qualitis.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.bml.client.BmlClient;
import com.webank.wedatasphere.linkis.bml.client.BmlClientFactory;
import com.webank.wedatasphere.linkis.bml.protocol.BmlUploadResponse;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QualitisRefExportOperation implements RefExportOperation<ExportRequestRef> {

    private final static Logger logger = LoggerFactory.getLogger(QualitisRefExportOperation.class);
    private static final String EXPORT_RULE_URL = "/qualitis/outer/api/v1/projector/rule/export";
    /**
     * Default user for BML download and upload.
     */
    private static final String DEFAULT_USER = "hadoop";

    DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    private String appId = "linkis_id";
    private String appToken = "a33693de51";

    public QualitisRefExportOperation(DevelopmentService developmentService){
        this.developmentService = developmentService;
        this.ssoRequestOperation = new OriginSSORequestOperation(QualitisAppConn.QUALITIS_APPCONN_NAME);
    }

    @Override
    public ResponseRef exportRef(ExportRequestRef requestRef) throws ExternalOperationFailedException {
        QualitisExportRequestRef qualitisExportRequestRef = (QualitisExportRequestRef) requestRef;
        QualitisExportResponseRef qualitisExportResponseRef = new QualitisExportResponseRef();
        Map params = qualitisExportRequestRef.getParameters();
        Long ruleGroupId = (Long) params.get("rule_group_id");
        String userName = (String) params.get("username");
        if (null == ruleGroupId || null == userName) {
            throw new ExternalOperationFailedException(90156, "Rule group ID is null when export.");
        }
        String url = null;
        try {
            url = HttpUtils.buildUrI(getBaseUrl(), EXPORT_RULE_URL + "/" + ruleGroupId.toString(), appId, appToken, RandomStringUtils
                        .randomNumeric(5)
                    , String.valueOf(System.currentTimeMillis())).toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error! Can not export rule group, rule group id: {}", ruleGroupId, e);
            throw new ExternalOperationFailedException(90156, "Error when building export url.", e);
        }
        QualitisGetAction qualitisGetAction = new QualitisGetAction();
        SSOUrlBuilderOperation ssoUrlBuilderOperation = qualitisExportRequestRef.getSsoUrlBuilderOperation();
        ssoUrlBuilderOperation.setAppName(QualitisAppConn.QUALITIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);

        ssoUrlBuilderOperation.setWorkspace(qualitisExportRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> resMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try {
            qualitisGetAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, qualitisGetAction);
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
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = (Map) resMap.get("data");
        String dataString = "";
        try {
            dataString = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.error("Error when parse export responses to json.", e);
            throw new ExternalOperationFailedException(90156, "Error when parse export responses to json.", e);
        }
        /**
         * BML client upload opeartion.
         */
        BmlClient bmlClient = BmlClientFactory.createBmlClient(DEFAULT_USER);
        BmlUploadResponse bmlUploadResponse = bmlClient.uploadResource(DEFAULT_USER,
            "Qualitis_exported_" + UUID.randomUUID().toString(), new ByteArrayInputStream(dataString.getBytes(StandardCharsets.UTF_8)));
        Map resourceMap = new HashMap();
        resourceMap.put("resourceId", bmlUploadResponse.resourceId());
        resourceMap.put("version", bmlUploadResponse.version());
        qualitisExportResponseRef.setResourceMap(resourceMap);
        return qualitisExportResponseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        developmentService = service;
    }

    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl();
    }

}
