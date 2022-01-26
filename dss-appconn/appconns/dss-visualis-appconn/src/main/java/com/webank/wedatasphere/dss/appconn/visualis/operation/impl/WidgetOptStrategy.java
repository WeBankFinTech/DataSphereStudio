package com.webank.wedatasphere.dss.appconn.visualis.operation.impl;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.enums.NodeIdEnum;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisDeleteAction;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPostAction;
import com.webank.wedatasphere.dss.appconn.visualis.model.WidgetResultData;
import com.webank.wedatasphere.dss.appconn.visualis.model.publish.VisualisCommonResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.operation.OperationStrategy;
import com.webank.wedatasphere.dss.appconn.visualis.operation.VisualisRefUpdateOperation;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisExportResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.*;
import com.webank.wedatasphere.dss.appconn.visualis.utils.*;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.ref.*;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.SSOIntegrationConf;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.common.io.resultset.ResultSetWriter;
import org.apache.linkis.cs.common.utils.CSCommonUtils;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.storage.domain.Column;
import org.apache.linkis.storage.domain.DataType;
import org.apache.linkis.storage.resultset.table.TableMetaData;
import org.apache.linkis.storage.resultset.table.TableRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class WidgetOptStrategy implements OperationStrategy {
    private final static Logger logger = LoggerFactory.getLogger(WidgetOptStrategy.class);

    @Override
    public ResponseRef createRef(NodeRequestRef requestRef, String baseUrl, DevelopmentService developmentService, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        String url = baseUrl + URLUtils.widgetUrl;
        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getUserName());
        visualisPostAction.addRequestPayload("widgetName", requestRef.getName());
        visualisPostAction.addRequestPayload("projectId", requestRef.getParameter("projectId"));
        visualisPostAction.addRequestPayload("description", requestRef.getJobContent().get("desc"));
        visualisPostAction.addRequestPayload(CSCommonUtils.CONTEXT_ID_STR, requestRef.getJobContent().get(CSCommonUtils.CONTEXT_ID_STR));
        if (requestRef.getJobContent().get("bindViewKey") != null) {
            String viewNodeName = requestRef.getJobContent().get("bindViewKey").toString();
            if (StringUtils.isNotBlank(viewNodeName) && !"empty".equals(viewNodeName)) {
                viewNodeName = getNodeNameByKey(viewNodeName, (String) requestRef.getJobContent().get("json"));
                visualisPostAction.addRequestPayload(CSCommonUtils.NODE_NAME_STR, viewNodeName);
            }
        }
        // 执行http请求，获取响应结果
        VisualisCommonResponseRef responseRef = VisualisCommonUtil.getResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
        if (responseRef.isFailed()) {
            logger.error(responseRef.getResponseBody());
            throw new ExternalOperationFailedException(90178, responseRef.getErrorMsg());
        }
        // cs
        @SuppressWarnings("unchecked")
        Map<String, Object> jobContent = (Map<String, Object>) responseRef.toMap().get("data");
        VisualisRefUpdateOperation visualisRefUpdateOperation = new VisualisRefUpdateOperation(developmentService);
        NodeUpdateCSRequestRefImpl visualisUpdateCSRequestRef = new NodeUpdateCSRequestRefImpl();
        visualisUpdateCSRequestRef.setContextID((String) requestRef.getJobContent().get(CSCommonUtils.CONTEXT_ID_STR));
        visualisUpdateCSRequestRef.setJobContent(jobContent);
        visualisUpdateCSRequestRef.setUserName(requestRef.getUserName());
        visualisUpdateCSRequestRef.setNodeType(requestRef.getNodeType());
        visualisUpdateCSRequestRef.setWorkspace(requestRef.getWorkspace());
        visualisRefUpdateOperation.updateRef(visualisUpdateCSRequestRef);

        responseRef.updateResponseBody(jobContent);
        return responseRef;
    }


    private String getNodeNameByKey(String key, String json) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        JsonArray nodeJsonArray = jsonObject.getAsJsonArray("nodes");
        List<DSSNode> dwsNodes = DSSCommonUtils.COMMON_GSON.fromJson(nodeJsonArray, new TypeToken<List<DSSNodeDefault>>() {
        }.getType());
        return dwsNodes.stream().filter(n -> key.equals(n.getId())).map(DSSNode::getName).findFirst().orElse("");
    }


    @Override
    public void deleteRef(String baseUrl, NodeRequestRef visualisDeleteRequestRef, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        String url;
        try {
            url = baseUrl + URLUtils.widgetDeleteUrl + "/" + VisualisNodeUtils.getId(visualisDeleteRequestRef);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Delete Widget Exception", e);
        }
        VisualisDeleteAction deleteAction = new VisualisDeleteAction();
        deleteAction.setUser(visualisDeleteRequestRef.getUserName());

        Map resMap = VisualisCommonUtil.getResponseMap(visualisDeleteRequestRef, ssoRequestOperation, url, deleteAction);
        @SuppressWarnings("unchecked")
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = (int) header.get("code");
        if (code != 200) {
            String errorMsg = header.toString();
            throw new ExternalOperationFailedException(90177, errorMsg, null);
        }
    }


    @Override
    public ResponseRef exportRef(ExportRequestRef requestRef,
                                 String url,
                                 VisualisPostAction visualisPostAction,
                                 String externalContent,
                                 SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation)


            throws Exception {
        VisualisCommonResponseRef widgetCreateResponseRef = new VisualisCommonResponseRef(externalContent);
        visualisPostAction.addRequestPayload("widgetIds", ((Double) Double.parseDouble(widgetCreateResponseRef.getWidgetId())).longValue());
        HttpResult httpResult = VisualisCommonUtil.getHttpResult(requestRef, url, visualisPostAction, ssoRequestOperation);
        return new VisualisExportResponseRef(httpResult.getResponseBody());

    }


    @Override
    public ResponseRef query(VisualisOpenRequestRef visualisOpenRequestRef, String externalContent, Long projectId, String baseUrl) throws Exception {
        VisualisCommonResponseRef responseRef = new VisualisCommonResponseRef(externalContent);
        return VisualisCommonUtil.getResponseRef(visualisOpenRequestRef, projectId, baseUrl, URLUtils.WIDGET_JUMP_URL_FORMAT, responseRef.getWidgetId());
    }


    @Override
    public ResponseRef updateRef(UpdateRequestRef requestRef, NodeRequestRef visualisUpdateRequestRef, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        if (requestRef instanceof UpdateCSRequestRef) {
            return updateCsRef(visualisUpdateRequestRef, baseUrl, ssoRequestOperation);
        }

        String url = baseUrl + URLUtils.widgetUpdateUrl;
        VisualisPostAction postAction = new VisualisPostAction();
        try {
            postAction.addRequestPayload("id", Long.parseLong(VisualisNodeUtils.getId(visualisUpdateRequestRef)));
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Update Widget Exception", e);
        }
        postAction.addRequestPayload("name", visualisUpdateRequestRef.getName());
        postAction.addRequestPayload("description", visualisUpdateRequestRef.getJobContent().get("desc"));
        postAction.setUser(visualisUpdateRequestRef.getUserName());
        Map resMap = VisualisCommonUtil.getResponseMap(visualisUpdateRequestRef, ssoRequestOperation, url, postAction);
        int status = (int) resMap.get("status");
        if (status != 0) {
            String errorMsg = resMap.get("message").toString();
            throw new ExternalOperationFailedException(90177, errorMsg);
        }
        return new CommonResponseRef();
    }


    @Override
    public ResponseRef copyRef(VisualisCopyRequestRef requestRef,
                               Map<String, Object> jobContent,
                               String url,
                               String nodeType,
                               VisualisPostAction visualisPostAction,
                               SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        visualisPostAction.addRequestPayload(NodeIdEnum.WIDGET_IDS.getName(), VisualisCommonUtil.getNodeId(jobContent, "widgetId"));

        return getVisualisCopyResponseRef(requestRef, jobContent, url, visualisPostAction, ssoRequestOperation);
    }

    private VisualisCopyResponseRef getVisualisCopyResponseRef(VisualisCopyRequestRef requestRef, Map<String, Object> jobContent, String url, VisualisPostAction visualisPostAction, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        VisualisCopyResponseRef responseRef;
        try {
            HttpResult httpResult = VisualisCommonUtil.getHttpResult(requestRef, url, visualisPostAction, ssoRequestOperation);
            if (httpResult.getStatusCode() != 200) {
                throw new ExternalOperationFailedException(90175, "copy Widget Exception" + httpResult.getResponseBody());
            }
            String responseBody = httpResult.getResponseBody();
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = SSOIntegrationConf.gson().fromJson(responseBody, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
            String id = NumberUtils.parseDoubleString(jobContent.get("widgetId").toString());
            @SuppressWarnings("unchecked")
            Map<String, Object> widgetData = (Map<String, Object>) dataMap.get("widget");
            jobContent.put("widgetId", Double.parseDouble(widgetData.get(id).toString()));
            responseRef = new VisualisCopyResponseRef(jobContent, responseBody);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "copy Widget Exception", e);
        }
        return responseRef;
    }

    @Override
    public ResponseRef importRef(VisualisPostAction visualisPostAction,
                                 String url,
                                 ImportRequestRef requestRef,
                                 SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                 DevelopmentService developmentService) throws ExternalOperationFailedException {

        VisualisImportResponseRef responseRef = (VisualisImportResponseRef) VisualisCommonUtil.getResponseRef(visualisPostAction, url, requestRef, ssoRequestOperation, "widget");

        // cs更新
        Map<String, Object> jobContent = responseRef.toMap();
        VisualisRefUpdateOperation visualisRefUpdateOperation = new VisualisRefUpdateOperation(developmentService);
        NodeUpdateCSRequestRefImpl visualisUpdateCSRequestRef = new NodeUpdateCSRequestRefImpl();
        visualisUpdateCSRequestRef.setContextID(requestRef.getParameter(CSCommonUtils.CONTEXT_ID_STR).toString());
        visualisUpdateCSRequestRef.setJobContent(jobContent);
        visualisUpdateCSRequestRef.setUserName(requestRef.getParameter("user").toString());
        visualisUpdateCSRequestRef.setNodeType(requestRef.getParameter("nodeType").toString());
        visualisUpdateCSRequestRef.setWorkspace(requestRef.getWorkspace());
        visualisRefUpdateOperation.updateRef(visualisUpdateCSRequestRef);

        responseRef.updateResponseBody(jobContent);
        return responseRef;
    }


    @Override
    public ResponseRef executeRef(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {

        String url = URLUtils.getUrl(baseUrl, URLUtils.WIDGET_DATA_URL_FORMAT, getId(ref));
        ref.getExecutionRequestRefContext().appendLog("Ready to get result set from " + url);
        List<Column> columns = Lists.newArrayList();
        VisualisDownloadAction visualisDownloadAction = new VisualisDownloadAction();
        visualisDownloadAction.setUser(VisualisCommonUtil.getUser(ref));
        try {
            VisualisCommonUtil.getHttpResult(ref, ssoRequestOperation, url, visualisDownloadAction);
            WidgetResultData responseData = BDPJettyServerHelper.gson().fromJson(IOUtils.toString(visualisDownloadAction.getInputStream()), WidgetResultData.class);
            if (responseData.getData().getColumns().isEmpty()) {
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
            resultSetWriter.flush();
            IOUtils.closeQuietly(resultSetWriter);
            ref.getExecutionRequestRefContext().sendResultSet(resultSetWriter);
        } catch (Throwable e) {
            ref.getExecutionRequestRefContext().appendLog("Failed to debug Widget url " + url);
            ref.getExecutionRequestRefContext().appendLog(e.getMessage());
            throw new ExternalOperationFailedException(90176, "Failed to debug Widget", e);
        } finally {
            IOUtils.closeQuietly(visualisDownloadAction.getInputStream());
        }
        return new VisualisCompletedExecutionResponseRef(200);
    }

    @Override
    public String getId(AsyncExecutionRequestRef requestRef) {
        try {
            String executionContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(requestRef.getJobContent());
            VisualisCommonResponseRef widgetCreateResponseRef = new VisualisCommonResponseRef(executionContent);
            return NumberUtils.parseDoubleString(widgetCreateResponseRef.getWidgetId());
        } catch (Exception e) {
            logger.error("failed to parse jobContent when execute widget node", e);
        }
        return null;
    }

    @Override
    public String submit(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        return null;
    }

    @Override
    public RefExecutionState state(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String execId) throws ExternalOperationFailedException {
        return null;
    }

    @Override
    public ResponseRef getAsyncResult(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String execId) throws ExternalOperationFailedException {
        return null;
    }


    private ResponseRef updateCsRef(NodeRequestRef visualisUpdateCSRequestRef,
                                    String baseUrl,
                                    SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        String url = baseUrl + URLUtils.widgetContextUrl;
        VisualisPostAction postAction = new VisualisPostAction();
        try {
            postAction.addRequestPayload("id", Integer.parseInt(VisualisNodeUtils.getId(visualisUpdateCSRequestRef)));
            postAction.addRequestPayload(CSCommonUtils.CONTEXT_ID_STR, visualisUpdateCSRequestRef.getContextID());
            postAction.setUser(visualisUpdateCSRequestRef.getUserName());
            SSOUrlBuilderOperation ssoUrlBuilderOperation = visualisUpdateCSRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
            ssoUrlBuilderOperation.setAppName(VisualisAppConn.VISUALIS_APPCONN_NAME);
            ssoUrlBuilderOperation.setReqUrl(url);
            ssoUrlBuilderOperation.setWorkspace(visualisUpdateCSRequestRef.getWorkspace().getWorkspaceName());
            postAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, postAction);
            String response = httpResult.getResponseBody();
            Map resMap = BDPJettyServerHelper.jacksonJson().readValue(response, Map.class);
            int status = (int) resMap.get("status");
            if (status != 0) {
                String errorMsg = resMap.get("message").toString();
                throw new ExternalOperationFailedException(90177, errorMsg);
            }
            return new CommonResponseRef();
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Update CS Exception", e);
        }
    }

}
