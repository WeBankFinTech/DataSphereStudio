package com.webank.wedatasphere.dss.appconn.visualis.operation.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.visualis.enums.NodeIdEnum;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisDeleteAction;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPostAction;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPutAction;
import com.webank.wedatasphere.dss.appconn.visualis.model.publish.VisualisCommonResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.operation.OperationStrategy;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisExportResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.*;
import com.webank.wedatasphere.dss.appconn.visualis.utils.*;
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
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.conf.ServerConfiguration;
import org.apache.linkis.storage.LineMetaData;
import org.apache.linkis.storage.LineRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;

public class DashboardOptStrategy implements OperationStrategy {
    private final static Logger logger = LoggerFactory.getLogger(DashboardOptStrategy.class);


    @Override
    public ResponseRef createRef(NodeRequestRef requestRef, String baseUrl, DevelopmentService developmentService, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        String url = baseUrl + URLUtils.dashboardPortalUrl;
        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getUserName());
        visualisPostAction.addRequestPayload("name", requestRef.getName());
        visualisPostAction.addRequestPayload("projectId", requestRef.getParameter("projectId"));
        visualisPostAction.addRequestPayload("avatar", "18");
        visualisPostAction.addRequestPayload("publish", true);
        visualisPostAction.addRequestPayload("description", requestRef.getJobContent().get("desc"));


        // 执行http请求，获取响应结果
        VisualisCommonResponseRef dashboardPortalResponseRef = VisualisCommonUtil.getResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
        VisualisCommonResponseRef dashboardRes = createDashboard(dashboardPortalResponseRef, requestRef, baseUrl, ssoRequestOperation);
        Map<String, Object> dashboardPayload = (Map<String, Object>) dashboardRes.toMap().get("payload");
        dashboardRes.updateResponseBody(dashboardPayload);
        return dashboardRes;
    }


    @Override
    public void deleteRef(String baseUrl, NodeRequestRef visualisDeleteRequestRef, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        String url;
        String portalId;
        try {
            portalId = VisualisNodeUtils.getId(visualisDeleteRequestRef);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Delete Dashboard failed when get portalId id ", e);
        }
        if (StringUtils.isEmpty(portalId)) {
            throw new ExternalOperationFailedException(90177, "Delete Dashboard failed for portalId id is null", null);
        }
        try {
            url = baseUrl + URLUtils.dashboardPortalUrl + "/" + portalId;
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Delete Dashboard Exception", e);
        }
        VisualisDeleteAction deleteAction = new VisualisDeleteAction();
        deleteAction.setUser(visualisDeleteRequestRef.getUserName());

        VisualisCommonUtil.checkResponseMap(VisualisCommonUtil.getResponseMap(visualisDeleteRequestRef, ssoRequestOperation, url, deleteAction));
    }


    private VisualisCommonResponseRef createDashboard(VisualisCommonResponseRef dashboardCreateResponseRef, NodeRequestRef requestRef, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        //dashboardCreateResponseRef保存有dashboardPortal的值，此时从dashboardPortal中取id作为dashboardPortalId
        Map<String, Object> payload = (Map<String, Object>) dashboardCreateResponseRef.toMap().get("payload");
        dashboardCreateResponseRef.updateResponseBody(payload);
        String portalId = NumberUtils.parseDoubleString(dashboardCreateResponseRef.toMap().get("id").toString());
        String url = baseUrl + URLUtils.dashboardPortalUrl + "/" + portalId + "/dashboards";

        VisualisPostAction visualisPostAction = new VisualisPostAction();
        visualisPostAction.setUser(requestRef.getUserName());
        visualisPostAction.addRequestPayload("config", "");
        visualisPostAction.addRequestPayload("dashboardPortalId", Long.parseLong(portalId));
        visualisPostAction.addRequestPayload("index", 0);
        visualisPostAction.addRequestPayload("name", requestRef.getName());
        visualisPostAction.addRequestPayload("parentId", 0);
        visualisPostAction.addRequestPayload("type", 1);
        VisualisCommonResponseRef commonResponseRef = VisualisCommonUtil.getResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
        VisualisCommonUtil.checkResponseMap(commonResponseRef.toMap());
        return commonResponseRef;
    }


    @Override
    public ResponseRef exportRef(ExportRequestRef requestRef,
                                 String url,
                                 VisualisPostAction visualisPostAction,
                                 String externalContent,
                                 SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws Exception {
        VisualisCommonResponseRef dashboardCreateResponseRef = new VisualisCommonResponseRef(externalContent);
        String portalId = dashboardCreateResponseRef.getDashboardPortalId();
        if (StringUtils.isEmpty(portalId)) {
            throw new ExternalOperationFailedException(90177, "export Dashboard failed for portalId id is null", null);
        }
        visualisPostAction.addRequestPayload("dashboardPortalIds", Long.parseLong(NumberUtils.parseDoubleString(portalId)));

        HttpResult httpResult = VisualisCommonUtil.getHttpResult(requestRef, url, visualisPostAction, ssoRequestOperation);
        return new VisualisExportResponseRef(httpResult.getResponseBody());
    }


    @Override
    public ResponseRef query(VisualisOpenRequestRef visualisOpenRequestRef, String externalContent, Long projectId, String baseUrl) throws Exception {
        VisualisCommonResponseRef responseRef = new VisualisCommonResponseRef(externalContent);
        return VisualisCommonUtil.getResponseRef(visualisOpenRequestRef, projectId, baseUrl, URLUtils.DASHBOARD_JUMP_URL_FORMAT, responseRef.getDashboardId());
    }


    @Override
    public ResponseRef updateRef(UpdateRequestRef requestRef, NodeRequestRef visualisUpdateRequestRef, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        String url;
        String id;
        try {
            id = VisualisNodeUtils.getId(visualisUpdateRequestRef);
            if (StringUtils.isEmpty(id)) {
                throw new ExternalOperationFailedException(90177, "Update Dashboard Exception, id is null");
            }
            url = baseUrl + URLUtils.dashboardPortalUrl + "/" + id;
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Update Dashboard Exception", e);
        }
        VisualisPutAction putAction = new VisualisPutAction();
        putAction.addRequestPayload("projectId", visualisUpdateRequestRef.getProjectId());
        putAction.addRequestPayload("name", visualisUpdateRequestRef.getName());
        putAction.addRequestPayload("id", Long.parseLong(id));
        putAction.addRequestPayload("avatar", "9");
        putAction.addRequestPayload("description", visualisUpdateRequestRef.getJobContent().get("desc"));
        putAction.addRequestPayload("publish", true);
        putAction.addRequestPayload("roleIds", Lists.newArrayList());
        putAction.setUser(visualisUpdateRequestRef.getUserName());

        VisualisCommonUtil.checkResponseMap(VisualisCommonUtil.getResponseMap(visualisUpdateRequestRef, ssoRequestOperation, url, putAction));
        return new CommonResponseRef();
    }


    @Override
    public ResponseRef copyRef(VisualisCopyRequestRef requestRef,
                               Map<String, Object> jobContent,
                               String url,
                               String nodeType,
                               VisualisPostAction visualisPostAction,
                               SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {

        visualisPostAction.addRequestPayload(NodeIdEnum.DASHBOARD_PORTAL_IDS.getName(), VisualisCommonUtil.getNodeId(jobContent, "dashboardPortalId"));
        return VisualisCommonUtil.getResponseRef(requestRef, jobContent, url, visualisPostAction, ssoRequestOperation, "dashboardPortal");
    }


    @Override
    public ResponseRef importRef(VisualisPostAction visualisPostAction, String url, ImportRequestRef requestRef, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, DevelopmentService developmentService) throws ExternalOperationFailedException {
        return VisualisCommonUtil.getResponseRef(visualisPostAction, url, requestRef, ssoRequestOperation);
    }


    @Override
    public ResponseRef executeRef(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        String previewUrl = URLUtils.getUrl(baseUrl, URLUtils.DASHBOARD_PREVIEW_URL_FORMAT, getId(ref));
        logger.info("previewUrl:{}", previewUrl);
        ref.getExecutionRequestRefContext().appendLog("Ready to get result set from " + previewUrl);
        VisualisDownloadAction previewDownloadAction = new VisualisDownloadAction();
        previewDownloadAction.setUser(VisualisCommonUtil.getUser(ref));

        VisualisDownloadAction metadataDownloadAction = new VisualisDownloadAction();
        metadataDownloadAction.setUser(VisualisCommonUtil.getUser(ref));

        try {
            logger.info("got workspace" + ref.getWorkspace());
            SSOUrlBuilderOperation ssoUrlBuilderOperation = VisualisCommonUtil.getSSOUrlBuilderOperation(ref, previewUrl);
            logger.info("got getSSOUrlBuilderOperation:" + SSOIntegrationConf.gson().toJson(ssoUrlBuilderOperation));
            logger.info("got getSSOUrlBuilderOperation built url:" + ssoUrlBuilderOperation.getBuiltUrl());
            previewDownloadAction.setURL(ssoUrlBuilderOperation.getBuiltUrl());
            ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, previewDownloadAction);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            IOUtils.copy(previewDownloadAction.getInputStream(), os);
            String response = new String(Base64.getEncoder().encode(os.toByteArray()));

            String metaUrl = URLUtils.getUrl(baseUrl, URLUtils.DASHBOARD_METADATA_URL_FORMAT, getId(ref));
            logger.info("metaUrl:{}", metaUrl);
            SSOUrlBuilderOperation ssoUrlBuilderOperationMeta = VisualisCommonUtil.getSSOUrlBuilderOperation(ref, metaUrl);
            metadataDownloadAction.setURL(ssoUrlBuilderOperationMeta.getBuiltUrl());
            ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperationMeta, metadataDownloadAction);
            String metadata = org.apache.commons.lang3.StringUtils.chomp(IOUtils.toString(metadataDownloadAction.getInputStream(), ServerConfiguration.BDP_SERVER_ENCODING().getValue()));
            ResultSetWriter resultSetWriter = ref.getExecutionRequestRefContext().createPictureResultSetWriter();
            resultSetWriter.addMetaData(new LineMetaData(metadata));
            resultSetWriter.addRecord(new LineRecord(response));
            resultSetWriter.flush();
            IOUtils.closeQuietly(resultSetWriter);
            ref.getExecutionRequestRefContext().sendResultSet(resultSetWriter);
        } catch (Throwable e) {
            ref.getExecutionRequestRefContext().appendLog("Failed to debug Dashboard url " + previewUrl);
            throw new ExternalOperationFailedException(90176, "Failed to debug Dashboard", e);
        } finally {
            IOUtils.closeQuietly(previewDownloadAction.getInputStream());
            IOUtils.closeQuietly(metadataDownloadAction.getInputStream());
        }
        return new VisualisCompletedExecutionResponseRef(200);
    }

    @Override
    public String getId(AsyncExecutionRequestRef requestRef) {
        try {
            String executionContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(requestRef.getJobContent());
            VisualisCommonResponseRef dashboardCreateResponseRef = new VisualisCommonResponseRef(executionContent);
            return NumberUtils.parseDoubleString(dashboardCreateResponseRef.getDashboardPortalId());
        } catch (Exception e) {
            logger.error("failed to parse jobContent when execute dashboard node", e);
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


}
