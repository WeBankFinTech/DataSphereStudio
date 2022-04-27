package com.webank.wedatasphere.dss.appconn.visualis.operation.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.visualis.constant.VisualisConstant;
import com.webank.wedatasphere.dss.appconn.visualis.utils.NumberUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.QueryJumpUrlResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.utils.DSSJobContentConstant;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSDeleteAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSDownloadAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPutAction;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.io.resultset.ResultSetWriter;
import org.apache.linkis.server.conf.ServerConfiguration;
import org.apache.linkis.storage.LineMetaData;
import org.apache.linkis.storage.LineRecord;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DashboardOptStrategy extends AbstractOperationStrategy {

    @Override
    public String getStrategyName() {
        return VisualisConstant.DASHBOARD_OPERATION_STRATEGY;
    }

    @Override
    public RefJobContentResponseRef createRef(ThirdlyRequestRef.DSSJobContentWithContextRequestRef requestRef) throws ExternalOperationFailedException {
        String url = baseUrl + URLUtils.dashboardPortalUrl;
        DSSPostAction visualisPostAction = new DSSPostAction();
        visualisPostAction.setUser(requestRef.getUserName());
        visualisPostAction.addRequestPayload("name", requestRef.getName());
        visualisPostAction.addRequestPayload("projectId", requestRef.getProjectRefId());
        visualisPostAction.addRequestPayload("avatar", "18");
        visualisPostAction.addRequestPayload("publish", true);
        visualisPostAction.addRequestPayload("description", requestRef.getDSSJobContent().get(DSSJobContentConstant.NODE_DESC_KEY));
        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(((EnvDSSLabel) (requestRef.getDSSLabels().get(0))).getEnv());
        visualisPostAction.addRequestPayload("labels", routeVO);
        // 执行http请求，获取响应结果
        ResponseRef dashboardPortalResponseRef = VisualisCommonUtil.getExternalResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
        return createDashboard(dashboardPortalResponseRef, requestRef);
    }


    @Override
    public void deleteRef(ThirdlyRequestRef.RefJobContentRequestRefImpl visualisDeleteRequestRef) throws ExternalOperationFailedException {
        String portalId = getDashboardPortalId(visualisDeleteRequestRef.getRefJobContent());
        if (StringUtils.isEmpty(portalId)) {
            throw new ExternalOperationFailedException(90177, "Delete Dashboard failed for portalId id is null", null);
        }
        String url = baseUrl + URLUtils.dashboardPortalUrl + "/" + portalId;
        DSSDeleteAction deleteAction = new DSSDeleteAction();
        deleteAction.setUser(visualisDeleteRequestRef.getUserName());
        deleteAction.setParameter("labels", ((EnvDSSLabel) (visualisDeleteRequestRef.getDSSLabels().get(0))).getEnv());
        VisualisCommonUtil.getExternalResponseRef(visualisDeleteRequestRef, ssoRequestOperation, url, deleteAction);
    }


    private RefJobContentResponseRef createDashboard(ResponseRef dashboardCreateResponseRef,
                                                     ThirdlyRequestRef.DSSJobContentWithContextRequestRef requestRef) throws ExternalOperationFailedException {
        //dashboardCreateResponseRef保存有dashboardPortal的值，此时从dashboardPortal中取id作为dashboardPortalId
        String portalId = NumberUtils.parseDoubleString(dashboardCreateResponseRef.toMap().get("id").toString());
        String url = baseUrl + URLUtils.dashboardPortalUrl + "/" + portalId + "/dashboards";

        DSSPostAction visualisPostAction = new DSSPostAction();
        visualisPostAction.setUser(requestRef.getUserName());
        visualisPostAction.addRequestPayload("config", "");
        visualisPostAction.addRequestPayload("dashboardPortalId", Long.parseLong(portalId));
        visualisPostAction.addRequestPayload("index", 0);
        visualisPostAction.addRequestPayload("name", requestRef.getName());
        visualisPostAction.addRequestPayload("parentId", 0);
        visualisPostAction.addRequestPayload("type", 1);
        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(((EnvDSSLabel) (requestRef.getDSSLabels().get(0))).getEnv());
        visualisPostAction.addRequestPayload("labels", routeVO);
        return VisualisCommonUtil.getRefJobContentResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
    }


    @Override
    public ExportResponseRef exportRef(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef,
                                       String url,
                                       DSSPostAction visualisPostAction) throws ExternalOperationFailedException {
        String portalId = getDashboardPortalId(requestRef.getRefJobContent());
        if (StringUtils.isEmpty(portalId)) {
            throw new ExternalOperationFailedException(90177, "export Dashboard failed for portalId id is null", null);
        }
        visualisPostAction.addRequestPayload("dashboardPortalIds", Long.parseLong(NumberUtils.parseDoubleString(portalId)));
        return VisualisCommonUtil.getExportResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
    }


    @Override
    public QueryJumpUrlResponseRef query(ThirdlyRequestRef.QueryJumpUrlRequestRefImpl visualisOpenRequestRef) {
        String dashboardId = getDashboardPortalId(visualisOpenRequestRef.getRefJobContent());
        Long projectId = visualisOpenRequestRef.getProjectRefId();
        return getQueryResponseRef(visualisOpenRequestRef, projectId, URLUtils.DASHBOARD_JUMP_URL_FORMAT, dashboardId);
    }


    @Override
    public ResponseRef updateRef(ThirdlyRequestRef.UpdateWitContextRequestRefImpl requestRef) throws ExternalOperationFailedException {
        String id = getDashboardPortalId(requestRef.getRefJobContent());
        if (StringUtils.isEmpty(id)) {
            throw new ExternalOperationFailedException(90177, "Update Dashboard Exception, id is null");
        }
        String url = baseUrl + URLUtils.dashboardPortalUrl + "/" + id;
        DSSPutAction putAction = new DSSPutAction();
        putAction.addRequestPayload("projectId", requestRef.getProjectRefId());
        putAction.addRequestPayload("name", requestRef.getName());
        putAction.addRequestPayload("id", Long.parseLong(id));
        putAction.addRequestPayload("avatar", "9");
        putAction.addRequestPayload("description", requestRef.getRefJobContent().get(DSSJobContentConstant.NODE_DESC_KEY));
        putAction.addRequestPayload("publish", true);
        putAction.addRequestPayload("roleIds", Lists.newArrayList());
        putAction.setUser(requestRef.getUserName());

        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(((EnvDSSLabel) (requestRef.getDSSLabels().get(0))).getEnv());
        putAction.addRequestPayload("labels", routeVO);

        return VisualisCommonUtil.getExternalResponseRef(requestRef, ssoRequestOperation, url, putAction);
    }


    @Override
    public RefJobContentResponseRef copyRef(ThirdlyRequestRef.CopyWitContextRequestRefImpl requestRef,
                               String url,
                               DSSPostAction visualisPostAction) throws ExternalOperationFailedException {
        String oldDashboardPortalId = getDashboardPortalId(requestRef.getRefJobContent());
        visualisPostAction.addRequestPayload(VisualisConstant.DASHBOARD_PORTAL_IDS, oldDashboardPortalId);
        InternalResponseRef responseRef1 = VisualisCommonUtil.getInternalResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
        String dashboardId = NumberUtils.parseDoubleString(requestRef.getRefJobContent().get("id").toString());
        @SuppressWarnings("unchecked")
        Map<String, Object> dashboardData = (Map<String, Object>) responseRef1.getData().get("dashboard");
        Map<String, Object> refJobContent = new HashMap<>(2);
        refJobContent.put("id", Double.parseDouble(dashboardData.get(dashboardId).toString()));
        refJobContent.put("projectId", requestRef.getProjectRefId());

        //dashboardPortal
        String dashboardPortalId = NumberUtils.parseDoubleString(requestRef.getRefJobContent().get("dashboardPortalId").toString());
        @SuppressWarnings("unchecked")
        Map<String, Object> dashboardPortalData = (Map<String, Object>) responseRef1.getData().get("dashboardPortal");
        refJobContent.put("dashboardPortalId", Double.parseDouble(dashboardPortalData.get(dashboardPortalId).toString()));
        return RefJobContentResponseRef.newBuilder().setRefJobContent(refJobContent).success();
    }


    @Override
    public RefJobContentResponseRef importRef(ThirdlyRequestRef.ImportWitContextRequestRefImpl requestRef,
                                              String url,
                                              DSSPostAction visualisPostAction) throws ExternalOperationFailedException {
        InternalResponseRef responseRef = VisualisCommonUtil.getInternalResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
        Map<String, Object> jobContent = new HashMap<>(3);
        jobContent.put("projectId", requestRef.getProjectRefId());
        String dashboardPortalId = getDashboardPortalId(requestRef.getRefJobContent());
        @SuppressWarnings("unchecked")
        Map<String, Object> dashboardPortal =(Map<String, Object>) responseRef.getData().get("dashboardPortal");
        jobContent.put("dashboardPortalId", Double.parseDouble(dashboardPortal.get(dashboardPortalId).toString()));

        String dashboardId = NumberUtils.parseDoubleString(requestRef.getRefJobContent().get("id").toString());
        Map<String, Object> dashboard = (Map<String, Object>) responseRef.getData().get("dashboard");
        jobContent.put("id", Double.parseDouble(dashboard.get(dashboardId).toString()));
        return RefJobContentResponseRef.newBuilder().setRefJobContent(jobContent).success();
    }

    @Override
    public ResponseRef executeRef(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref) throws ExternalOperationFailedException {
        String previewUrl = URLUtils.getUrl(baseUrl, URLUtils.DASHBOARD_PREVIEW_URL_FORMAT, getDashboardPortalId(ref.getRefJobContent()));
        logger.info("The {} of Visualis try to execute ref RefJobContent: {} in previewUrl {}.", ref.getType(), ref.getRefJobContent(), previewUrl);
        ref.getExecutionRequestRefContext().appendLog(String.format("The %s of Visualis try to execute ref RefJobContent: %s in previewUrl %s.", ref.getType(), ref.getRefJobContent(), previewUrl));
        DSSDownloadAction previewDownloadAction = new DSSDownloadAction();
        previewDownloadAction.setUser(ref.getUserName());
        previewDownloadAction.setParameter("labels", ((EnvDSSLabel) (ref.getDSSLabels().get(0))).getEnv());

        DSSDownloadAction metadataDownloadAction = new DSSDownloadAction();
        metadataDownloadAction.setUser(ref.getUserName());
        metadataDownloadAction.setParameter("labels", ((EnvDSSLabel) (ref.getDSSLabels().get(0))).getEnv());
        try {
            VisualisCommonUtil.getHttpResult(ref, ssoRequestOperation, previewUrl, previewDownloadAction);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            IOUtils.copy(previewDownloadAction.getInputStream(), os);
            String response = new String(Base64.getEncoder().encode(os.toByteArray()));

            String metaUrl = URLUtils.getUrl(baseUrl, URLUtils.DASHBOARD_METADATA_URL_FORMAT, getDashboardPortalId(ref.getRefJobContent()));
            logger.info("The {} of Visualis try to execute ref RefJobContent: {} in metaUrl {}.", ref.getType(), ref.getRefJobContent(), previewUrl);
            ref.getExecutionRequestRefContext().appendLog(String.format("The %s of Visualis try to execute ref RefJobContent: %s in metaUrl %s.", ref.getType(), ref.getRefJobContent(), previewUrl));
            VisualisCommonUtil.getHttpResult(ref, ssoRequestOperation, metaUrl, metadataDownloadAction);
            String metadata = org.apache.commons.lang3.StringUtils.chomp(IOUtils.toString(metadataDownloadAction.getInputStream(), ServerConfiguration.BDP_SERVER_ENCODING().getValue()));
            ResultSetWriter resultSetWriter = ref.getExecutionRequestRefContext().createPictureResultSetWriter();
            resultSetWriter.addMetaData(new LineMetaData(metadata));
            resultSetWriter.addRecord(new LineRecord(response));
            resultSetWriter.flush();
            IOUtils.closeQuietly(resultSetWriter);
            ref.getExecutionRequestRefContext().sendResultSet(resultSetWriter);
        } catch (Throwable e) {
            ref.getExecutionRequestRefContext().appendLog("Failed to execute Dashboard url " + previewUrl);
            throw new ExternalOperationFailedException(90176, "Failed to debug Dashboard", e);
        } finally {
            IOUtils.closeQuietly(previewDownloadAction);
            IOUtils.closeQuietly(metadataDownloadAction);
        }
        return ResponseRef.newExternalBuilder().success();
    }

    private String getDashboardPortalId(Map<String, Object> refJobContent) {
        String dashboardPortalId = refJobContent.get("dashboardPortalId").toString();
        return NumberUtils.parseDoubleString(dashboardPortalId);
    }


}
