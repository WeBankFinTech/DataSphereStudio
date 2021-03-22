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

package com.webank.wedatasphere.dss.appconn.visualis.execution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.visualis.model.WidgetResultData;
import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.DashboardCreateResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.DisplayCreateResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.WidgetCreateResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.utils.NumberUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisDownloadAction;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionLogListener;
import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionResultListener;
import com.webank.wedatasphere.dss.standard.app.development.execution.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.execution.common.AsyncExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.SSOIntegrationConf;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.common.io.resultset.ResultSetWriter;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import com.webank.wedatasphere.linkis.server.conf.ServerConfiguration;
import com.webank.wedatasphere.linkis.storage.LineMetaData;
import com.webank.wedatasphere.linkis.storage.LineRecord;
import com.webank.wedatasphere.linkis.storage.domain.Column;
import com.webank.wedatasphere.linkis.storage.domain.DataType;
import com.webank.wedatasphere.linkis.storage.resultset.table.TableMetaData;
import com.webank.wedatasphere.linkis.storage.resultset.table.TableRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class VisualisRefExecutionOperation implements RefExecutionOperation {

    private final static Logger logger = LoggerFactory.getLogger(VisualisRefExecutionOperation.class);
    DevelopmentService developmentService;
    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public VisualisRefExecutionOperation(DevelopmentService service) {
        this.developmentService = service;
        this.ssoRequestOperation = new OriginSSORequestOperation(this.developmentService.getAppDesc().getAppName());
    }

    @Override
    public ResponseRef execute(ExecutionRequestRef ref) throws ExternalOperationFailedException {
        AsyncExecutionRequestRef asyncExecutionRequestRef = (AsyncExecutionRequestRef) ref;
        String nodeType = asyncExecutionRequestRef.getExecutionRequestRefContext().getRuntimeMap().get("nodeType").toString();
        if("visualis.widget".equalsIgnoreCase(nodeType)){
            return executeWidget(asyncExecutionRequestRef);
        } else if("visualis.display".equalsIgnoreCase(nodeType)){
            return executePreview(asyncExecutionRequestRef,
                    URLUtils.getUrl(getBaseUrl(), URLUtils.DISPLAY_PREVIEW_URL_FORMAT, getId(asyncExecutionRequestRef)),
                    URLUtils.getUrl(getBaseUrl(), URLUtils.DISPLAY_METADATA_URL_FORMAT, getId(asyncExecutionRequestRef)));
        } else if("visualis.dashboard".equalsIgnoreCase(nodeType)){
            return executePreview(asyncExecutionRequestRef,
                    URLUtils.getUrl(getBaseUrl(), URLUtils.DASHBOARD_PREVIEW_URL_FORMAT, getId(asyncExecutionRequestRef)),
                    URLUtils.getUrl(getBaseUrl(), URLUtils.DASHBOARD_METADATA_URL_FORMAT, getId(asyncExecutionRequestRef)));
        } else {
            throw new ExternalOperationFailedException(90177, "Unknown task type " + nodeType, null);
        }
    }


    private ResponseRef executeWidget(AsyncExecutionRequestRef ref) throws ExternalOperationFailedException {
        String url = URLUtils.getUrl(getBaseUrl(), URLUtils.WIDGET_DATA_URL_FORMAT, getId(ref));
        ref.getExecutionRequestRefContext().appendLog("Ready to get result set from " + url);
        List<Column> columns = Lists.newArrayList();
        List<TableRecord> tableRecords = Lists.newArrayList();
        VisualisDownloadAction visualisDownloadAction = new VisualisDownloadAction();
        visualisDownloadAction.setUser(getUser(ref));
        SSOUrlBuilderOperation ssoUrlBuilderOperation = ref.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(ref.getWorkspace().getWorkspaceName());
        try{
            visualisDownloadAction.setURL(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisDownloadAction);
            WidgetResultData responseData = BDPJettyServerHelper.gson().fromJson(IOUtils.toString(visualisDownloadAction.getInputStream()), WidgetResultData.class);
            if(responseData.getData().getColumns().isEmpty()){
                ref.getExecutionRequestRefContext().appendLog("Cannot execute an empty Widget!");
                throw new ExternalOperationFailedException(90176, "Cannot execute an empty Widget!", null);
            }
            for (WidgetResultData.Column columnData : responseData.getData().getColumns()) {
                columns.add(new Column(columnData.getName(), DataType.toDataType(columnData.getType().toLowerCase()), ""));
            }
            ResultSetWriter resultSetWriter = ref.getExecutionRequestRefContext().createTableResultSetWriter();
            resultSetWriter.addMetaData(new TableMetaData(columns.toArray(new Column[0])));
            for (Map<String, Object> recordMap : responseData.getData().getResultList()) {
                resultSetWriter.addRecord(new TableRecord(recordMap.values().toArray()));
            }
        } catch (Throwable e){
            ref.getExecutionRequestRefContext().appendLog("Failed to debug Widget url " + url);
            throw new ExternalOperationFailedException(90176, "Failed to debug Widget", e);
        } finally {
            IOUtils.closeQuietly(visualisDownloadAction.getInputStream());
        }
        return new VisualisCompletedExecutionResponseRef(200);
    }

    private ResponseRef executePreview(AsyncExecutionRequestRef ref, String previewUrl, String metaUrl) throws ExternalOperationFailedException {
        ref.getExecutionRequestRefContext().appendLog("Ready to get result set from " + previewUrl);
        VisualisDownloadAction previewDownloadAction = new VisualisDownloadAction();
        previewDownloadAction.setUser(getUser(ref));

        VisualisDownloadAction metadataDownloadAction = new VisualisDownloadAction();
        metadataDownloadAction.setUser(getUser(ref));

        try{
            logger.info("got workspace" + ref.getWorkspace());
            SSOUrlBuilderOperation ssoUrlBuilderOperation = ref.getWorkspace().getSSOUrlBuilderOperation().copy();
            ssoUrlBuilderOperation.setAppName(developmentService.getAppDesc().getAppName());
            ssoUrlBuilderOperation.setReqUrl(previewUrl);
            ssoUrlBuilderOperation.setWorkspace(ref.getWorkspace().getWorkspaceName());
            logger.info("got getSSOUrlBuilderOperation:" + SSOIntegrationConf.gson().toJson(ssoUrlBuilderOperation));
            logger.info("got getSSOUrlBuilderOperation built url:" + ssoUrlBuilderOperation.getBuiltUrl());
            previewDownloadAction.setURL(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult previewResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, previewDownloadAction);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            IOUtils.copy(previewDownloadAction.getInputStream(), os);
            String response = new String(Base64.getEncoder().encode(os.toByteArray()));

            ResultSetWriter resultSetWriter = ref.getExecutionRequestRefContext().createPictureResultSetWriter();
            resultSetWriter.addRecord(new LineRecord(response));

            SSOUrlBuilderOperation ssoUrlBuilderOperationMeta = ref.getWorkspace().getSSOUrlBuilderOperation().copy();
            ssoUrlBuilderOperationMeta.setAppName(developmentService.getAppDesc().getAppName());
            ssoUrlBuilderOperationMeta.setReqUrl(metaUrl);
            ssoUrlBuilderOperationMeta.setWorkspace(ref.getWorkspace().getWorkspaceName());
            metadataDownloadAction.setURL(ssoUrlBuilderOperationMeta.getBuiltUrl());
            HttpResult metaResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperationMeta, metadataDownloadAction);
            String metadata = StringUtils.chomp(IOUtils.toString(metadataDownloadAction.getInputStream(), ServerConfiguration.BDP_SERVER_ENCODING().getValue()));
            resultSetWriter.addMetaData(new LineMetaData(metadata));
        } catch (Throwable e){
            ref.getExecutionRequestRefContext().appendLog("Failed to debug Display url " + previewUrl);
            logger.error(e.getMessage(), e);
            throw new ExternalOperationFailedException(90176, "Failed to debug Display", e);
        } finally {
            IOUtils.closeQuietly(previewDownloadAction.getInputStream());
            IOUtils.closeQuietly(metadataDownloadAction.getInputStream());
        }
        return new VisualisCompletedExecutionResponseRef(200);
    }

    private String getUser(AsyncExecutionRequestRef requestRef) {
        return requestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.linkis.schedulis.submit.user").toString();
    }

    private String getId(AsyncExecutionRequestRef requestRef) {
        try {
            String executionContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(requestRef.getJobContent());
            String nodeType = requestRef.getExecutionRequestRefContext().getRuntimeMap().get("nodeType").toString();
            if("visualis.display".equalsIgnoreCase(nodeType)){
                DisplayCreateResponseRef displayCreateResponseRef = new DisplayCreateResponseRef(executionContent);
                return NumberUtils.parseDoubleString(displayCreateResponseRef.getDisplayId());
            } else if("visualis.dashboard".equalsIgnoreCase(nodeType)){
                DashboardCreateResponseRef dashboardCreateResponseRef = new DashboardCreateResponseRef(executionContent);
                return NumberUtils.parseDoubleString(dashboardCreateResponseRef.getDashboardPortalId());
            } else if ("visualis.widget".equalsIgnoreCase(nodeType)){
                WidgetCreateResponseRef widgetCreateResponseRef = new WidgetCreateResponseRef(executionContent);
                return NumberUtils.parseDoubleString(widgetCreateResponseRef.getWidgetId());
            }
        } catch (Exception e) {
            logger.error("failed to parse jobContent", e);
        }
        return null;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }

    private String getBaseUrl(){
        return developmentService.getAppInstance().getBaseUrl();
    }
}
