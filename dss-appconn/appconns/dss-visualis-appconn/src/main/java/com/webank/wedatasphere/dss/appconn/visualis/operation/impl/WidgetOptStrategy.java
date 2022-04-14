package com.webank.wedatasphere.dss.appconn.visualis.operation.impl;

import com.webank.wedatasphere.dss.appconn.visualis.constant.VisualisConstant;
import com.webank.wedatasphere.dss.appconn.visualis.utils.NumberUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.QueryJumpUrlResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.utils.DSSJobContentConstant;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSDeleteAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.cs.common.utils.CSCommonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidgetOptStrategy extends AbstractOperationStrategy {

    @Override
    public String getStrategyName() {
        return VisualisConstant.WIDGET_OPERATION_STRATEGY;
    }

    @Override
    public RefJobContentResponseRef createRef(ThirdlyRequestRef.DSSJobContentWithContextRequestRef requestRef) throws ExternalOperationFailedException {
        String url = baseUrl + URLUtils.widgetUrl;
        DSSPostAction postAction = new DSSPostAction();
        postAction.setUser(requestRef.getUserName());
        postAction.addRequestPayload("widgetName", requestRef.getName());
        postAction.addRequestPayload("projectId", requestRef.getProjectRefId());
        postAction.addRequestPayload("description", requestRef.getDSSJobContent().get(DSSJobContentConstant.NODE_DESC_KEY));
        postAction.addRequestPayload(CSCommonUtils.CONTEXT_ID_STR, requestRef.getContextId());
        if (requestRef.getDSSJobContent().containsKey(DSSJobContentConstant.UP_STREAM_KEY)) {
            List<DSSNode> dssNodes = (List<DSSNode>) requestRef.getDSSJobContent().get(DSSJobContentConstant.UP_STREAM_KEY);
            postAction.addRequestPayload(CSCommonUtils.NODE_NAME_STR, dssNodes.get(0).getName());
        }
        // 执行http请求，获取响应结果
        RefJobContentResponseRef responseRef = VisualisCommonUtil.getRefJobContentResponseRef(requestRef, ssoRequestOperation, url, postAction);
        // update cs
        updateCsRef((RefJobContentRequestRef) requestRef, requestRef.getContextId());
        return responseRef;
    }

    @Override
    public void deleteRef(ThirdlyRequestRef.RefJobContentRequestRefImpl visualisDeleteRequestRef) throws ExternalOperationFailedException {
        String url = baseUrl + URLUtils.widgetDeleteUrl + "/" + getWidgetId(visualisDeleteRequestRef.getRefJobContent());
        DSSDeleteAction deleteAction = new DSSDeleteAction();
        deleteAction.setUser(visualisDeleteRequestRef.getUserName());

        VisualisCommonUtil.getExternalResponseRef(visualisDeleteRequestRef, ssoRequestOperation, url, deleteAction);
    }


    @Override
    public ExportResponseRef exportRef(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef,
                                        String url,
                                       DSSPostAction visualisPostAction) throws ExternalOperationFailedException {
        visualisPostAction.addRequestPayload("widgetIds", Long.parseLong(getWidgetId(requestRef.getRefJobContent())));
        return VisualisCommonUtil.getExportResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
    }


    @Override
    public QueryJumpUrlResponseRef query(ThirdlyRequestRef.QueryJumpUrlRequestRefImpl visualisOpenRequestRef) {
        String widgetId = getWidgetId(visualisOpenRequestRef.getRefJobContent());
        return getQueryResponseRef(visualisOpenRequestRef, visualisOpenRequestRef.getProjectRefId(), URLUtils.WIDGET_JUMP_URL_FORMAT, widgetId);
    }

    @Override
    public ResponseRef updateRef(ThirdlyRequestRef.UpdateWitContextRequestRefImpl requestRef) throws ExternalOperationFailedException {
        String url = baseUrl + URLUtils.widgetUpdateUrl;
        DSSPostAction postAction = new DSSPostAction();
        try {
            postAction.addRequestPayload("id", Long.parseLong(getWidgetId(requestRef.getRefJobContent())));
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Update Widget Exception", e);
        }
        postAction.addRequestPayload("name", requestRef.getName());
        postAction.addRequestPayload("description", requestRef.getRefJobContent().get(DSSJobContentConstant.NODE_DESC_KEY));
        postAction.setUser(requestRef.getUserName());

        return VisualisCommonUtil.getInternalResponseRef(requestRef, ssoRequestOperation, url, postAction);
    }


    @Override
    public RefJobContentResponseRef copyRef(ThirdlyRequestRef.CopyWitContextRequestRefImpl requestRef,
                                            String url,
                                            DSSPostAction visualisPostAction) throws ExternalOperationFailedException {
        visualisPostAction.addRequestPayload(VisualisConstant.WIDGET_IDS, getWidgetId(requestRef.getRefJobContent()));

        InternalResponseRef responseRef = VisualisCommonUtil.getInternalResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
        String id = getWidgetId(requestRef.getRefJobContent());
        @SuppressWarnings("unchecked")
        Map<String, Object> widgetData = (Map<String, Object>) responseRef.getData().get("widget");
        Map<String, Object> jobContent = new HashMap<>(1);
        jobContent.put("widgetId", Double.parseDouble(widgetData.get(id).toString()));
        return RefJobContentResponseRef.newBuilder().setRefJobContent(jobContent).success();
    }

    @Override
    public RefJobContentResponseRef importRef(ThirdlyRequestRef.ImportWitContextRequestRefImpl requestRef,
                                              String url,
                                              DSSPostAction visualisPostAction) throws ExternalOperationFailedException {

        InternalResponseRef responseRef = VisualisCommonUtil.getInternalResponseRef(requestRef, ssoRequestOperation, url, visualisPostAction);
        Map<String, Object> jobContent = new HashMap<>();
        String id = getWidgetId(requestRef.getRefJobContent());

        @SuppressWarnings("unchecked")
        Map<String, Object> widgetData =(Map<String, Object>) responseRef.getData().get("widget");
        jobContent.put("widgetId", Double.parseDouble(widgetData.get(id).toString()));

        // cs更新
        updateCsRef(requestRef, requestRef.getContextId());
        return RefJobContentResponseRef.newBuilder().setRefJobContent(jobContent).success();
    }


    @Override
    public ResponseRef executeRef(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref) throws ExternalOperationFailedException {
        String url = URLUtils.getUrl(baseUrl, URLUtils.WIDGET_DATA_URL_FORMAT, getWidgetId(ref.getRefJobContent()));
        return executeRef(ref, url);
    }

    private String getWidgetId(Map<String, Object> refJobContent) {
        return NumberUtils.parseDoubleString(refJobContent.get("widgetId").toString());
    }

    private ResponseRef updateCsRef(RefJobContentRequestRef requestRef,
                                    String contextId) throws ExternalOperationFailedException {
        String url = baseUrl + URLUtils.widgetContextUrl;
        DSSPostAction postAction = new DSSPostAction();
        postAction.addRequestPayload("id", Integer.parseInt(getWidgetId(requestRef.getRefJobContent())));
        postAction.addRequestPayload(CSCommonUtils.CONTEXT_ID_STR, contextId);
        postAction.setUser(requestRef.getUserName());
        return VisualisCommonUtil.getInternalResponseRef(requestRef, ssoRequestOperation, url, postAction);
    }

}
