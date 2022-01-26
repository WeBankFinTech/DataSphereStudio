package com.webank.wedatasphere.dss.appconn.visualis.operation.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.visualis.enums.NodeIdEnum;
import com.webank.wedatasphere.dss.appconn.visualis.enums.ViewExecStatusEnum;
import com.webank.wedatasphere.dss.appconn.visualis.model.*;
import com.webank.wedatasphere.dss.appconn.visualis.model.publish.VisualisCommonResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.operation.OperationStrategy;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisExportResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.*;
import com.webank.wedatasphere.dss.appconn.visualis.utils.*;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.ref.*;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.common.io.resultset.ResultSetWriter;
import org.apache.linkis.cs.client.ContextClient;
import org.apache.linkis.cs.client.builder.ContextClientFactory;
import org.apache.linkis.cs.client.utils.SerializeHelper;
import org.apache.linkis.cs.common.entity.enumeration.ContextType;
import org.apache.linkis.cs.common.entity.source.ContextID;
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

import java.io.InputStream;
import java.util.*;

public class ViewOptStrategy implements OperationStrategy {
    private final static Logger logger = LoggerFactory.getLogger(ViewOptStrategy.class);


    @Override
    public ResponseRef createRef(NodeRequestRef requestRef, String baseUrl, DevelopmentService developmentService, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {

        String url = baseUrl + URLUtils.VIEW_URL;
        logger.info("requestUrl:{}", url);

        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getUserName());
        visualisPostAction.addRequestPayload("name", requestRef.getName());
        visualisPostAction.addRequestPayload("projectId", requestRef.getParameter("projectId"));
        visualisPostAction.addRequestPayload("description", requestRef.getJobContent().get("desc"));
        visualisPostAction.addRequestPayload("sourceId", 0);
        visualisPostAction.addRequestPayload("config", "");
        visualisPostAction.addRequestPayload("sql", "");
        visualisPostAction.addRequestPayload("model", "");
        visualisPostAction.addRequestPayload("roles", Lists.newArrayList());

        // 执行http请求，获取响应结果
        VisualisCommonResponseRef responseRef = VisualisCommonUtil.getResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
        if (responseRef.isFailed()) {
            logger.error(responseRef.getResponseBody());
            throw new ExternalOperationFailedException(90178, responseRef.getErrorMsg());
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> jobContent = (Map<String, Object>) responseRef.toMap().get("payload");
            responseRef.updateResponseBody(jobContent);
            return responseRef;
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Create View Exception", e);
        }
    }


    @Override
    public void deleteRef(String baseUrl, NodeRequestRef visualisDeleteRequestRef, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        String url;
        try {
            url = baseUrl + URLUtils.VIEW_URL + "/" + VisualisNodeUtils.getId(visualisDeleteRequestRef);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Delete View Exception", e);
        }

        VisualisDeleteAction deleteAction = new VisualisDeleteAction();
        deleteAction.setUser(visualisDeleteRequestRef.getUserName());

        VisualisCommonUtil.checkResponseMap(VisualisCommonUtil.getResponseMap(visualisDeleteRequestRef, ssoRequestOperation, url, deleteAction));
    }


    @Override
    public ResponseRef exportRef(ExportRequestRef requestRef, String url, VisualisPostAction visualisPostAction, String externalContent, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws Exception {
        VisualisCommonResponseRef responseRef = new VisualisCommonResponseRef(externalContent);

        visualisPostAction.addRequestPayload("viewIds", ((Double) Double.parseDouble(responseRef.getViewId())).longValue());
        HttpResult httpResult = VisualisCommonUtil.getHttpResult(requestRef, url, visualisPostAction, ssoRequestOperation);
        return new VisualisExportResponseRef(httpResult.getResponseBody());
    }


    @Override
    public ResponseRef query(VisualisOpenRequestRef visualisOpenRequestRef, String externalContent, Long projectId, String baseUrl) throws Exception {
        VisualisCommonResponseRef responseRef = new VisualisCommonResponseRef(externalContent);
        return VisualisCommonUtil.getResponseRef(visualisOpenRequestRef, projectId, baseUrl, URLUtils.VIEW_JUMP_URL_FORMAT, responseRef.getViewId());
    }


    @Override
    public ResponseRef updateRef(UpdateRequestRef requestRef, NodeRequestRef visualisUpdateRequestRef, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        String url;
        String id;
        try {
            id = VisualisNodeUtils.getId(visualisUpdateRequestRef);
            url = baseUrl + URLUtils.VIEW_URL + "/" + id;
            logger.info("requestUrl:{}", url);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Update View Exception", e);
        }
        VisualisPutAction putAction = new VisualisPutAction();

        putAction.addRequestPayload("projectId", visualisUpdateRequestRef.getProjectId());
        putAction.addRequestPayload("name", visualisUpdateRequestRef.getName());
        putAction.addRequestPayload("id", Long.parseLong(id));
        putAction.addRequestPayload("description", visualisUpdateRequestRef.getJobContent().get("desc"));
        putAction.setUser(visualisUpdateRequestRef.getUserName());

        Map resMap = VisualisCommonUtil.getResponseMap(visualisUpdateRequestRef, ssoRequestOperation, url, putAction);
        @SuppressWarnings("unchecked")
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = Integer.parseInt(NumberUtils.parseDoubleString(header.get("code").toString()));
        if (code != 200) {
            String errorMsg = header.toString();
            if (errorMsg.contains("the view name is already taken")) {
                return new CommonResponseRef();
            } else {
                throw new ExternalOperationFailedException(90177, errorMsg, null);
            }
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

        visualisPostAction.addRequestPayload(NodeIdEnum.VIEW_IDS.getName(), VisualisCommonUtil.getNodeId(jobContent, "id"));

        return VisualisCommonUtil.getResponseRef(requestRef, jobContent, url, visualisPostAction, ssoRequestOperation, "view");
    }


    @Override
    public ResponseRef importRef(VisualisPostAction visualisPostAction,
                                 String url,
                                 ImportRequestRef requestRef,
                                 SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                 DevelopmentService developmentService) throws ExternalOperationFailedException {

        return VisualisCommonUtil.getResponseRef(visualisPostAction, url, requestRef, ssoRequestOperation);
    }


    @Override
    public ResponseRef executeRef(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {

        String url = URLUtils.getUrl(baseUrl, URLUtils.VIEW_DATA_URL_FORMAT, getId(ref));
        ref.getExecutionRequestRefContext().appendLog("dss execute view node,ready to get result set from " + url);

        VisualisDownloadAction visualisDownloadAction = new VisualisDownloadAction();
        visualisDownloadAction.setUser(VisualisCommonUtil.getUser(ref));

        try {
            VisualisCommonUtil.getHttpResult(ref, ssoRequestOperation, url, visualisDownloadAction);

        } catch (AppStandardErrorException e) {
            ref.getExecutionRequestRefContext().appendLog("dss execute view node failed, ssoRequestOperation error");
            throw new ExternalOperationFailedException(90176, "dss execute view node failed, ssoRequestOperation error", e);
        }
        InputStream inputStream = null;
        try {
            inputStream = Optional.of(visualisDownloadAction.getInputStream()).orElseThrow(() -> new ExternalOperationFailedException(90176, "DSS execute view node failed,inputStream is empty", null));
            ViewResultData responseData = BDPJettyServerHelper.gson().fromJson(IOUtils.toString(inputStream), ViewResultData.class);
            List<ViewResultData.Column> oldColumns = Optional.of(responseData.getData().getColumns()).orElseThrow(() -> new ExternalOperationFailedException(90176, "DSS execute view node failed,responseData is empty", null));
            if (oldColumns.isEmpty()) {
                ref.getExecutionRequestRefContext().appendLog("dss execute view node failed,columns is empty");
                throw new ExternalOperationFailedException(90176, "dss execute view node failed,columns is empty", null);
            }

            List<Column> columns = Lists.newArrayList();
            for (ViewResultData.Column columnData : oldColumns) {
                columns.add(new Column(columnData.getName(), DataType.toDataType(columnData.getType().toLowerCase()), ""));
            }
            ResultSetWriter resultSetWriter = ref.getExecutionRequestRefContext().createTableResultSetWriter();
            resultSetWriter.addMetaData(new TableMetaData(columns.toArray(new Column[0])));

            List<Map<String, Object>> oldResultList = Optional.of(responseData.getData().getResultList()).orElseThrow(() -> new ExternalOperationFailedException(90176, "DSS execute view node failed,resultList is empty", null));
            for (Map<String, Object> recordMap : oldResultList) {
                resultSetWriter.addRecord(new TableRecord(recordMap.values().toArray()));
            }
            resultSetWriter.flush();
            IOUtils.closeQuietly(resultSetWriter);
            ref.getExecutionRequestRefContext().sendResultSet(resultSetWriter);
            cleanCSTabel(ref);
        } catch (Throwable e) {
            ref.getExecutionRequestRefContext().appendLog("dss execute view node failed，url：" + url);
            ref.getExecutionRequestRefContext().appendLog(e.getMessage());
            throw new ExternalOperationFailedException(90176, "dss execute view node failed", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return new VisualisCompletedExecutionResponseRef(200);
    }

    @Override
    public String submit(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        String url = URLUtils.getUrl(baseUrl, URLUtils.VIEW_DATA_URL_SUBMIT, getId(ref));
        ref.getExecutionRequestRefContext().appendLog("dss execute view node,ready to submit from " + url);
        VisualisDownloadAction visualisDownloadAction = new VisualisDownloadAction();
        visualisDownloadAction.setUser(VisualisCommonUtil.getUser(ref));
        InputStream inputStream = null;
        try {
            VisualisCommonUtil.getHttpResult(ref, ssoRequestOperation, url, visualisDownloadAction);
            inputStream = Optional.of(visualisDownloadAction.getInputStream()).orElseThrow(() -> new ExternalOperationFailedException(90176, "DSS execute view node failed,inputStream is empty", null));
            Map map = BDPJettyServerHelper.gson().fromJson(IOUtils.toString(inputStream), Map.class);
            ref.getExecutionRequestRefContext().appendLog(map.toString());
            @SuppressWarnings("unchecked")
            Map dataMap = Optional.of((Map<String, Object>) map.get("data")).orElseThrow(() -> new ExternalOperationFailedException(90176, "judge datasource type failed"));
            @SuppressWarnings("unchecked")
            Map paginateWithExecStatusMap = (Map<String, Object>) dataMap.get("paginateWithExecStatus");
            return Optional.of(paginateWithExecStatusMap.get("execId").toString()).orElseThrow(() -> new ExternalOperationFailedException(90176, "judge datasource type failed"));
        } catch (Exception e) {
            ref.getExecutionRequestRefContext().appendLog("dss execute view error for submit failed，url：" + url);
            ref.getExecutionRequestRefContext().appendLog(e.getMessage());
            throw new ExternalOperationFailedException(90176, "dss execute view error for submit failed,", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public RefExecutionState state(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String execId) throws ExternalOperationFailedException {
        if (StringUtils.isEmpty(execId)) {
            ref.getExecutionRequestRefContext().appendLog("dss execute view error for execId is null when get state!");
            throw new ExternalOperationFailedException(90176, "dss execute view error when get state");
        }
        String url = URLUtils.getUrl(baseUrl, URLUtils.VIEW_DATA_URL_STATE, execId);
        ref.getExecutionRequestRefContext().appendLog("dss execute view node,ready to get state from " + url);

        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(VisualisCommonUtil.getUser(ref));

        try {
            HttpResult httpResult = VisualisCommonUtil.getHttpResult(ref, ssoRequestOperation, url, visualisPostAction);
            ref.getExecutionRequestRefContext().appendLog(httpResult.getResponseBody());

            Map responseMap = Optional.of(BDPJettyServerHelper.jacksonJson().readValue(httpResult.getResponseBody(), Map.class)).orElseThrow(() -> new ExternalOperationFailedException(90176, "DSS execute view node failed,responseMap is empty", null));
            String status = Optional.of(((Map<String, Object>) responseMap.get("payload")).get("status").toString()).orElseThrow(() -> new ExternalOperationFailedException(90176, "DSS execute view node failed,payload is empty", null));
            switch (ViewExecStatusEnum.getEnum(status)) {
                case Failed:
                    return RefExecutionState.Failed;
                case Succeed:
                    return RefExecutionState.Success;
                case Cancelled:
                    return RefExecutionState.Killed;
                default:
                    return RefExecutionState.Running;
            }
        } catch (Exception e) {
            ref.getExecutionRequestRefContext().appendLog("dss execute view error for get state failed，url：" + url);
            ref.getExecutionRequestRefContext().appendLog(e.getMessage());
            throw new ExternalOperationFailedException(90176, "dss execute view error for get state failed", e);
        }
    }

    @Override
    public ResponseRef getAsyncResult(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String execId) throws ExternalOperationFailedException {
        if (StringUtils.isEmpty(execId)) {
            ref.getExecutionRequestRefContext().appendLog("dss execute view error for execId is null when get result!");
            throw new ExternalOperationFailedException(90176, "dss execute view error when get result");
        }
        String url = URLUtils.getUrl(baseUrl, URLUtils.VIEW_DATA_URL_ASYNC_RESULT, execId);
        ref.getExecutionRequestRefContext().appendLog("dss execute view node,ready to get result set from " + url);
        VisualisPostAction visualisPostAction = new VisualisPostAction();

        visualisPostAction.setUser(VisualisCommonUtil.getUser(ref));
        try {
            HttpResult httpResult = VisualisCommonUtil.getHttpResult(ref, ssoRequestOperation, url, visualisPostAction);
            String responseBody = httpResult.getResponseBody();
            Map responseMap = Optional.of(BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class)).orElseThrow(() -> new ExternalOperationFailedException(90176, "DSS execute view node failed,responseMap is empty", null));
            Map<String, Object> payloadMap = Optional.of((Map<String, Object>) responseMap.get("payload")).orElseThrow(() -> new ExternalOperationFailedException(90176, "DSS execute view node failed,payloadMap is empty", null));
            ViewAsyncResultData responseData = BDPJettyServerHelper.gson().fromJson(BDPJettyServerHelper.gson().toJson(payloadMap), ViewAsyncResultData.class);
            ref.getExecutionRequestRefContext().appendLog("get responseData success ");
            List<ViewAsyncResultData.Column> oldColumns = Optional.of(responseData.getColumns()).orElseThrow(() -> new ExternalOperationFailedException(90176, "DSS execute view node failed,responseData is empty", null));
            if (oldColumns.isEmpty()) {
                ref.getExecutionRequestRefContext().appendLog("dss execute view node failed,columns is empty");
                throw new ExternalOperationFailedException(90176, "dss execute view node failed,columns is empty", null);
            }
            List<Column> columns = Lists.newArrayList();
            for (ViewAsyncResultData.Column columnData : oldColumns) {
                columns.add(new Column(columnData.getName(), DataType.toDataType(columnData.getType().toLowerCase()), ""));
            }
            ResultSetWriter resultSetWriter = ref.getExecutionRequestRefContext().createTableResultSetWriter();
            resultSetWriter.addMetaData(new TableMetaData(columns.toArray(new Column[0])));
            List<Map<String, Object>> oldResultList = Optional.of(responseData.getResultList()).orElseThrow(() -> new ExternalOperationFailedException(90176, "DSS execute view node failed,resultList is empty", null));
            for (Map<String, Object> recordMap : oldResultList) {
                resultSetWriter.addRecord(new TableRecord(recordMap.values().toArray()));
            }
            resultSetWriter.flush();
            IOUtils.closeQuietly(resultSetWriter);
            ref.getExecutionRequestRefContext().sendResultSet(resultSetWriter);
            cleanCSTabel(ref);
        } catch (Throwable e) {
            ref.getExecutionRequestRefContext().appendLog("dss execute view node failed，url：" + url);
            ref.getExecutionRequestRefContext().appendLog(e.getMessage());
            throw new ExternalOperationFailedException(90176, "dss execute view node failed", e);
        }
        return new VisualisCompletedExecutionResponseRef(200);
    }


    @Override
    public String getId(AsyncExecutionRequestRef requestRef) {
        try {
            String executionContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(requestRef.getJobContent());
            VisualisCommonResponseRef viewCreateResponseRef = new VisualisCommonResponseRef(executionContent);
            return NumberUtils.parseDoubleString(viewCreateResponseRef.getViewId());
        } catch (Exception e) {
            logger.error("failed to parse jobContent when execute view node", e);
        }
        return null;
    }

    // 为了实现View节点不产生CS表，对View执行产生的CS表进行清理
    private void cleanCSTabel(AsyncExecutionRequestRef ref) throws ErrorException {
        Map<String, Object> runMap = ref.getExecutionRequestRefContext().getRuntimeMap();
        String contextIdStr = (String) runMap.get("contextID");
        String nodeName = (String) runMap.get("nodeName");
        ContextID contextID  = SerializeHelper.deserializeContextID(contextIdStr);
        ContextClient contextClient = ContextClientFactory.getOrCreateContextClient();
        contextClient.removeAllValueByKeyPrefixAndContextType(contextID, ContextType.METADATA, CSCommonUtils.NODE_PREFIX + nodeName);
    }

}
