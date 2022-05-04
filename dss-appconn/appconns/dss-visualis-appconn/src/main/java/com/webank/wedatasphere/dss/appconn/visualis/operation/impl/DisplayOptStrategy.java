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
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.io.resultset.ResultSetWriter;
import org.apache.linkis.server.conf.ServerConfiguration;
import org.apache.linkis.storage.LineMetaData;
import org.apache.linkis.storage.LineRecord;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DisplayOptStrategy extends AbstractOperationStrategy {

    @Override
    public String getStrategyName() {
        return VisualisConstant.DISPLAY_OPERATION_STRATEGY;
    }

    @Override
    public RefJobContentResponseRef createRef(ThirdlyRequestRef.DSSJobContentWithContextRequestRef requestRef) throws ExternalOperationFailedException {
        String url = baseUrl + URLUtils.displayUrl;
        logger.info("requestUrl:{}", url);

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
        ResponseRef responseRef = VisualisCommonUtil.getExternalResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
        String displayId = responseRef.toMap().get("id").toString();
        Map<String, Object> jobContent = new HashMap<>(1);
        jobContent.put("displayId", displayId);
        createDisplaySlide(displayId, requestRef);
        return RefJobContentResponseRef.newBuilder().setRefJobContent(jobContent).success();
    }

    @Override
    public void deleteRef(ThirdlyRequestRef.RefJobContentRequestRefImpl visualisDeleteRequestRef) throws ExternalOperationFailedException {
        String url = baseUrl + URLUtils.displayUrl + "/" + getDisplayId(visualisDeleteRequestRef.getRefJobContent());
        // Delete协议在加入url label时会存在被nginx拦截转发情况，在这里换成Post协议对label进行兼容
        DSSPostAction deleteAction = new DSSPostAction();
        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(((EnvDSSLabel) (visualisDeleteRequestRef.getDSSLabels().get(0))).getEnv());
        deleteAction.addRequestPayload("labels", routeVO);
        deleteAction.setUser(visualisDeleteRequestRef.getUserName());
        VisualisCommonUtil.getExternalResponseRef(visualisDeleteRequestRef, ssoRequestOperation, url, deleteAction);
    }


    private void createDisplaySlide(String displayId, ThirdlyRequestRef.DSSJobContentWithContextRequestRef requestRef) throws ExternalOperationFailedException {
        String id = NumberUtils.parseDoubleString(displayId);
        String url = baseUrl + URLUtils.displayUrl + "/" + id + "/slides";
        DSSPostAction visualisPostAction = new DSSPostAction();
        visualisPostAction.setUser(requestRef.getUserName());
        visualisPostAction.addRequestPayload("config", URLUtils.displaySlideConfig);
        visualisPostAction.addRequestPayload("displayId", Long.parseLong(id));
        visualisPostAction.addRequestPayload("index", 0);

        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(((EnvDSSLabel) (requestRef.getDSSLabels().get(0))).getEnv());
        visualisPostAction.addRequestPayload("labels", routeVO);
        VisualisCommonUtil.getExternalResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
    }


    @Override
    public ExportResponseRef exportRef(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef,
                                       String url,
                                       DSSPostAction postAction) throws ExternalOperationFailedException {
        postAction.addRequestPayload("displayIds", getDisplayId(requestRef.getRefJobContent()));
        return VisualisCommonUtil.getExportResponseRef(requestRef, ssoRequestOperation, url, postAction);
    }

    @Override
    public QueryJumpUrlResponseRef query(ThirdlyRequestRef.QueryJumpUrlRequestRefImpl requestRef) {
        String displayId = getDisplayId(requestRef.getRefJobContent()).toString();
        return getQueryResponseRef(requestRef, requestRef.getProjectRefId(), URLUtils.DISPLAY_JUMP_URL_FORMAT, displayId);
    }

    private Long getDisplayId(Map<String, Object> refJobContent) {
        String displayId = refJobContent.get("displayId").toString();
        return Long.parseLong(NumberUtils.parseDoubleString(displayId));
    }

    @Override
    public ResponseRef updateRef(ThirdlyRequestRef.UpdateWitContextRequestRefImpl requestRef) throws ExternalOperationFailedException {
        long id = getDisplayId(requestRef.getRefJobContent());
        String url = baseUrl + URLUtils.displayUrl + "/" + id;
        DSSPutAction putAction = new DSSPutAction();
        putAction.addRequestPayload("projectId", requestRef.getProjectRefId());
        putAction.addRequestPayload("name", requestRef.getName());
        putAction.addRequestPayload("id", id);
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
                                            DSSPostAction postAction) throws ExternalOperationFailedException {
        Long id = getDisplayId(requestRef.getRefJobContent());
        postAction.addRequestPayload(VisualisConstant.DISPLAY_IDS, id);
        InternalResponseRef responseRef = VisualisCommonUtil.getInternalResponseRef(requestRef, ssoRequestOperation, url, postAction);
        @SuppressWarnings("unchecked")
        Map<String, Object> displayData = (Map<String, Object>) responseRef.getData().get("display");
        Map<String, Object> refJobContent = new HashMap<>(1);
        refJobContent.put("displayId", Double.parseDouble(displayData.get(id.toString()).toString()));
        return RefJobContentResponseRef.newBuilder().setRefJobContent(refJobContent).success();
    }


    @Override
    @SuppressWarnings("unchecked")
    public RefJobContentResponseRef importRef(ThirdlyRequestRef.ImportWitContextRequestRefImpl requestRef,
                                              String url,
                                              DSSPostAction visualisPostAction) throws ExternalOperationFailedException {
        InternalResponseRef responseRef = VisualisCommonUtil.getInternalResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
        Map<String, Object> jobContent = new HashMap<>(1);
        String id = getDisplayId(requestRef.getRefJobContent()).toString();

        Map<String, Object> displayData =(Map<String, Object>) responseRef.getData().get("display");
        jobContent.put("displayId", Double.parseDouble(displayData.get(id).toString()));
        return RefJobContentResponseRef.newBuilder().setRefJobContent(jobContent).success();
    }


    @Override
    public ResponseRef executeRef(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref) throws ExternalOperationFailedException {
        String previewUrl = URLUtils.getUrl(baseUrl, URLUtils.DISPLAY_PREVIEW_URL_FORMAT, getDisplayId(ref.getRefJobContent()).toString());
        logger.info("User {} try to execute Visualis display with refJobContent: {} in previewUrl {}.", ref.getExecutionRequestRefContext().getSubmitUser(),
                ref.getRefJobContent(), previewUrl);
        ref.getExecutionRequestRefContext().appendLog(String.format("The %s of Visualis try to execute ref RefJobContent: %s in previewUrl %s.", ref.getType(), ref.getRefJobContent(), previewUrl));
        DSSDownloadAction previewDownloadAction = new DSSDownloadAction();
        previewDownloadAction.setUser(ref.getExecutionRequestRefContext().getSubmitUser());
        previewDownloadAction.setParameter("labels", ((EnvDSSLabel) (ref.getDSSLabels().get(0))).getEnv());

        DSSDownloadAction metadataDownloadAction = new DSSDownloadAction();
        metadataDownloadAction.setUser(ref.getExecutionRequestRefContext().getSubmitUser());
        metadataDownloadAction.setParameter("labels", ((EnvDSSLabel) (ref.getDSSLabels().get(0))).getEnv());

        try {
            VisualisCommonUtil.getHttpResult(ref, ssoRequestOperation, previewUrl, previewDownloadAction);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            IOUtils.copy(previewDownloadAction.getInputStream(), os);
            String response = new String(Base64.getEncoder().encode(os.toByteArray()));

            String metaUrl = URLUtils.getUrl(baseUrl, URLUtils.DISPLAY_METADATA_URL_FORMAT, getDisplayId(ref.getRefJobContent()).toString());
            VisualisCommonUtil.getHttpResult(ref, ssoRequestOperation, metaUrl, metadataDownloadAction);
            String metadata = StringUtils.chomp(IOUtils.toString(metadataDownloadAction.getInputStream(), ServerConfiguration.BDP_SERVER_ENCODING().getValue()));
            ResultSetWriter resultSetWriter = ref.getExecutionRequestRefContext().createPictureResultSetWriter();
            resultSetWriter.addMetaData(new LineMetaData(metadata));
            resultSetWriter.addRecord(new LineRecord(response));
            resultSetWriter.flush();
            IOUtils.closeQuietly(resultSetWriter);
            ref.getExecutionRequestRefContext().sendResultSet(resultSetWriter);
        } catch (Throwable e) {
            ref.getExecutionRequestRefContext().appendLog("Failed to debug Display url " + previewUrl);
            throw new ExternalOperationFailedException(90176, "Failed to debug Display", e);
        } finally {
            IOUtils.closeQuietly(previewDownloadAction);
            IOUtils.closeQuietly(metadataDownloadAction);
        }
        return ResponseRef.newExternalBuilder().success();
    }

}
