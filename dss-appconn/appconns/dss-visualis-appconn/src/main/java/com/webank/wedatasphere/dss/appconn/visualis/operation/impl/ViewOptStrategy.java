package com.webank.wedatasphere.dss.appconn.visualis.operation.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.visualis.constant.VisualisConstant;
import com.webank.wedatasphere.dss.appconn.visualis.model.ViewAsyncResultData;
import com.webank.wedatasphere.dss.appconn.visualis.operation.AsyncExecutionOperationStrategy;
import com.webank.wedatasphere.dss.appconn.visualis.utils.NumberUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.QueryJumpUrlResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.utils.DSSJobContentConstant;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSDeleteAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSGetAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPutAction;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.common.io.resultset.ResultSetWriter;
import org.apache.linkis.cs.client.ContextClient;
import org.apache.linkis.cs.client.builder.ContextClientFactory;
import org.apache.linkis.cs.client.utils.SerializeHelper;
import org.apache.linkis.cs.common.entity.enumeration.ContextType;
import org.apache.linkis.cs.common.entity.source.ContextID;
import org.apache.linkis.cs.common.utils.CSCommonUtils;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.storage.domain.Column;
import org.apache.linkis.storage.domain.DataType;
import org.apache.linkis.storage.resultset.table.TableMetaData;
import org.apache.linkis.storage.resultset.table.TableRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ViewOptStrategy extends AbstractOperationStrategy implements AsyncExecutionOperationStrategy {
    private final static Logger logger = LoggerFactory.getLogger(ViewOptStrategy.class);

    @Override
    public String getStrategyName() {
        return VisualisConstant.VIEW_OPERATION_STRATEGY;
    }

    @Override
    public RefJobContentResponseRef createRef(ThirdlyRequestRef.DSSJobContentWithContextRequestRef requestRef) throws ExternalOperationFailedException {

        String url = baseUrl + URLUtils.VIEW_URL;
        DSSPostAction postAction = new DSSPostAction();
        postAction.setUser(requestRef.getUserName());
        postAction.addRequestPayload("name", requestRef.getName());
        postAction.addRequestPayload("projectId", requestRef.getProjectRefId());
        postAction.addRequestPayload("description", requestRef.getDSSJobContent().get(DSSJobContentConstant.NODE_DESC_KEY));
        postAction.addRequestPayload("sourceId", 0);
        postAction.addRequestPayload("config", "");
        postAction.addRequestPayload("sql", "");
        postAction.addRequestPayload("model", "");
        postAction.addRequestPayload("roles", Lists.newArrayList());

        // 执行http请求，获取响应结果
        return VisualisCommonUtil.getRefJobContentResponseRef(requestRef, ssoRequestOperation, url, postAction);
    }


    @Override
    public void deleteRef(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        String url = baseUrl + URLUtils.VIEW_URL + "/" + getId(requestRef.getRefJobContent());

        DSSDeleteAction deleteAction = new DSSDeleteAction();
        deleteAction.setUser(requestRef.getUserName());

        VisualisCommonUtil.getExternalResponseRef(requestRef, ssoRequestOperation, url, deleteAction);
    }


    @Override
    public ExportResponseRef exportRef(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef, String url,
                                       DSSPostAction visualisPostAction) throws ExternalOperationFailedException {
        visualisPostAction.addRequestPayload("viewIds", Double.parseDouble(getId(requestRef.getRefJobContent())));
        return VisualisCommonUtil.getExportResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
    }


    @Override
    public QueryJumpUrlResponseRef query(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef) {
        String id = getId(requestRef.getRefJobContent());
        return getQueryResponseRef(requestRef, requestRef.getProjectRefId(), URLUtils.VIEW_JUMP_URL_FORMAT, id);
    }


    @Override
    public ResponseRef updateRef(ThirdlyRequestRef.UpdateWitContextRequestRefImpl requestRef) throws ExternalOperationFailedException {
        String id = getId(requestRef.getRefJobContent());
        String url = baseUrl + URLUtils.VIEW_URL + "/" + id;
        logger.info("requestUrl: {}.", url);
        DSSPutAction putAction = new DSSPutAction();
        putAction.addRequestPayload("projectId", requestRef.getProjectRefId());
        putAction.addRequestPayload("name", requestRef.getName());
        putAction.addRequestPayload("id", Long.parseLong(id));
        putAction.addRequestPayload("description", requestRef.getRefJobContent().get(DSSJobContentConstant.NODE_DESC_KEY));
        putAction.setUser(requestRef.getUserName());

        return VisualisCommonUtil.getExternalResponseRef(requestRef, ssoRequestOperation, url, putAction);
    }


    @Override
    public RefJobContentResponseRef copyRef(ThirdlyRequestRef.CopyWitContextRequestRefImpl requestRef,
                                            String url,
                                            DSSPostAction postAction) throws ExternalOperationFailedException {

        postAction.addRequestPayload(VisualisConstant.VIEW_IDS, getId(requestRef.getRefJobContent()));
        InternalResponseRef responseRef = VisualisCommonUtil.getInternalResponseRef(requestRef, ssoRequestOperation, url, postAction);
        String id = getId(requestRef.getRefJobContent());
        @SuppressWarnings("unchecked")
        Map<String, Object> viewData = (Map<String, Object>) responseRef.getData().get("view");
        Map<String, Object> jobContent = new HashMap<>(2);
        jobContent.put("id", Double.parseDouble(viewData.get(id).toString()));
        return RefJobContentResponseRef.newBuilder().setRefJobContent(jobContent).success();
    }


    @Override
    public RefJobContentResponseRef importRef(ThirdlyRequestRef.ImportWitContextRequestRefImpl requestRef,
                                              String url,
                                              DSSPostAction visualisPostAction) throws ExternalOperationFailedException {

        InternalResponseRef responseRef = VisualisCommonUtil.getInternalResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
        Map<String, Object> jobContent = new HashMap<>(2);
        String id = getId(requestRef.getRefJobContent());

        @SuppressWarnings("unchecked")
        Map<String, Object> viewData = (Map<String, Object>) responseRef.getData().get("view");
        jobContent.put("projectId", requestRef.getParameter("projectId"));
        jobContent.put("id", Double.parseDouble(viewData.get(id).toString()));
        return RefJobContentResponseRef.newBuilder().setRefJobContent(jobContent).success();
    }

    @Override
    public ResponseRef executeRef(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref) throws ExternalOperationFailedException {
        String url = URLUtils.getUrl(baseUrl, URLUtils.VIEW_DATA_URL_FORMAT, getId(ref.getRefJobContent()));
        ResponseRef responseRef = executeRef(ref, url);
        try {
            cleanCSTabel(ref);
        } catch (ErrorException e) {
            ref.getExecutionRequestRefContext().appendLog("ERROR: Failed to clean cs tables. Caused by: " + ExceptionUtils.getRootCauseMessage(e));
            throw new ExternalOperationFailedException(90176, "Failed to clean cs tables.", e);
        }
        return responseRef;
    }

    @Override
    public String submit(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref) throws ExternalOperationFailedException {
        String url = URLUtils.getUrl(baseUrl, URLUtils.VIEW_DATA_URL_SUBMIT, getId(ref.getRefJobContent()));
        logger.info("User {} try to submit Visualis view with refJobContent: {} in url {}.", ref.getExecutionRequestRefContext().getSubmitUser(),
                ref.getRefJobContent(), url);
        ref.getExecutionRequestRefContext().appendLog("dss execute view node, ready to submit to " + url);
        DSSGetAction visualisGetAction = new DSSGetAction();
        visualisGetAction.setUser(ref.getUserName());
        InternalResponseRef responseRef = VisualisCommonUtil.getInternalResponseRef(ref, ssoRequestOperation, url, visualisGetAction);
        Map<String, Object> paginateWithExecStatusMap = (Map<String, Object>) responseRef.getData().get("paginateWithExecStatus");
        return paginateWithExecStatusMap.get("execId").toString();
    }

    @Override
    public RefExecutionState state(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref, String execId) throws ExternalOperationFailedException {
        if (StringUtils.isEmpty(execId)) {
            ref.getExecutionRequestRefContext().appendLog("dss execute view error for execId is null when get state!");
            throw new ExternalOperationFailedException(90176, "dss execute view error when get state");
        }
        String url = URLUtils.getUrl(baseUrl, URLUtils.VIEW_DATA_URL_STATE, execId);
        ref.getExecutionRequestRefContext().appendLog("dss execute view node, ready to get state from " + url);

        DSSPostAction visualisPostAction = new DSSPostAction();
        visualisPostAction.setUser(ref.getExecutionRequestRefContext().getSubmitUser());

        ResponseRef responseRef = VisualisCommonUtil.getExternalResponseRef(ref, ssoRequestOperation, url, visualisPostAction);
        ref.getExecutionRequestRefContext().appendLog(responseRef.getResponseBody());

        String status = responseRef.toMap().get("status").toString();
        return toRefExecutionState(status);
    }

    @Override
    public ExecutionResponseRef getAsyncResult(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref, String execId) throws ExternalOperationFailedException {
        if (StringUtils.isEmpty(execId)) {
            ref.getExecutionRequestRefContext().appendLog("dss execute view error for execId is null when get result!");
            throw new ExternalOperationFailedException(90176, "dss execute view error when get result");
        }
        String url = URLUtils.getUrl(baseUrl, URLUtils.VIEW_DATA_URL_ASYNC_RESULT, execId);
        ref.getExecutionRequestRefContext().appendLog("dss execute view node,ready to get result set from " + url);
        DSSPostAction visualisPostAction = new DSSPostAction();

        visualisPostAction.setUser(ref.getExecutionRequestRefContext().getSubmitUser());
        try {
            ResponseRef responseRef = VisualisCommonUtil.getExternalResponseRef(ref, ssoRequestOperation, url, visualisPostAction);
            ViewAsyncResultData responseData = BDPJettyServerHelper.gson().fromJson(BDPJettyServerHelper.gson().toJson(responseRef.toMap()), ViewAsyncResultData.class);
            ref.getExecutionRequestRefContext().appendLog("get responseData success.");
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
        return ExecutionResponseRef.newBuilder().success();
    }


    public String getId(Map<String, Object> requestRef) {
        return NumberUtils.parseDoubleString(requestRef.get("id").toString());
    }

    // 为了实现View节点不产生CS表，对View执行产生的CS表进行清理
    private void cleanCSTabel(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref) throws ErrorException {
        String contextIdStr = ref.getContextId();
        String nodeName = ref.getName();
        ContextID contextID  = SerializeHelper.deserializeContextID(contextIdStr);
        ContextClient contextClient = ContextClientFactory.getOrCreateContextClient();
        contextClient.removeAllValueByKeyPrefixAndContextType(contextID, ContextType.METADATA, CSCommonUtils.NODE_PREFIX + nodeName);
    }

}
