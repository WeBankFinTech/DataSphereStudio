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

import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPostAction;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VisualisRefImportOperation implements RefImportOperation<ImportRequestRef> {

    private final static Logger logger = LoggerFactory.getLogger(VisualisRefImportOperation.class);

    private DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public VisualisRefImportOperation(DevelopmentService developmentService) {
        this.developmentService = developmentService;
        this.ssoRequestOperation = this.developmentService.getSSORequestService().createSSORequestOperation(VisualisAppConn.VISUALIS_APPCONN_NAME);
    }

    @Override
    public ResponseRef importRef(ImportRequestRef requestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.projectUrl + "/import";
        logger.info("url:{}", url);
        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getParameter("user").toString());
        if(null == requestRef.getParameter("projectId") || StringUtils.isEmpty(requestRef.getParameter("projectId").toString())){
            throw new  ExternalOperationFailedException(100067,"导入节点Visualis工程ID为空");
        }
        visualisPostAction.addRequestPayload("projectId", requestRef.getParameter("projectId"));
        visualisPostAction.addRequestPayload("projectVersion", "v1");
        visualisPostAction.addRequestPayload("flowVersion", requestRef.getParameter("orcVersion"));
        visualisPostAction.addRequestPayload("resourceId", requestRef.getParameter("resourceId"));
        visualisPostAction.addRequestPayload("version", requestRef.getParameter("version"));

        String nodeType = requestRef.getParameter("nodeType").toString().toLowerCase();
        return ModuleFactory.getInstance().crateModule(nodeType).importRef(visualisPostAction, url, requestRef, ssoRequestOperation, developmentService);

    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

    private String getBaseUrl() {
        return developmentService.getAppInstance().getBaseUrl();
    }

}
