package com.webank.wedatasphere.dss.appconn.visualis.utils;

import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSHttpAction;
import com.webank.wedatasphere.dss.standard.app.sso.ref.WorkspaceRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class VisualisCommonUtil {

    private final static Logger logger = LoggerFactory.getLogger(VisualisCommonUtil.class);

    public static SSOUrlBuilderOperation getSSOUrlBuilderOperation(WorkspaceRequestRef requestRef, String url) {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = SSOHelper.createSSOUrlBuilderOperation(requestRef.getWorkspace());
        ssoUrlBuilderOperation.setAppName(VisualisAppConn.VISUALIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        return ssoUrlBuilderOperation;
    }

    public static HttpResult getHttpResult(WorkspaceRequestRef requestRef,
                                           SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                           String url,
                                           DSSHttpAction visualisHttpAction) throws ExternalOperationFailedException {

        try {
            SSOUrlBuilderOperation ssoUrlBuilderOperation = getSSOUrlBuilderOperation(requestRef, url);
            visualisHttpAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            return ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisHttpAction);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Create visualis node Exception", e);
        }
    }

    public static InternalResponseRef getInternalResponseRef(WorkspaceRequestRef requestRef,
                                                             SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                                             String url,
                                                             DSSHttpAction visualisHttpAction) throws ExternalOperationFailedException {
        HttpResult httpResult = getHttpResult(requestRef, ssoRequestOperation, url, visualisHttpAction);
        InternalResponseRef responseRef = ResponseRef.newInternalBuilder().setResponseBody(httpResult.getResponseBody()).build();
        checkResponseRef(responseRef);
        return responseRef;
    }

    public static RefJobContentResponseRef getRefJobContentResponseRef(WorkspaceRequestRef requestRef,
                                                          SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                                          String url,
                                                          DSSHttpAction visualisHttpAction) throws ExternalOperationFailedException {
        ResponseRef responseRef = getExternalResponseRef(requestRef, ssoRequestOperation, url, visualisHttpAction);
        return RefJobContentResponseRef.newBuilder().setRefJobContent(responseRef.toMap()).success();
    }

    public static ExportResponseRef getExportResponseRef(WorkspaceRequestRef requestRef,
                                                         SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                                         String url,
                                                         DSSHttpAction visualisHttpAction) throws ExternalOperationFailedException {
        ResponseRef responseRef = getExternalResponseRef(requestRef, ssoRequestOperation, url, visualisHttpAction);
        return ExportResponseRef.newBuilder().setResourceMap(responseRef.toMap()).success();
    }

    public static ResponseRef getExternalResponseRef(WorkspaceRequestRef requestRef,
                                                         SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                                         String url,
                                                         DSSHttpAction visualisHttpAction) throws ExternalOperationFailedException {
        HttpResult httpResult = getHttpResult(requestRef, ssoRequestOperation, url, visualisHttpAction);
        logger.info("responsebody from visualis:{}", httpResult.getResponseBody());
        ResponseRef responseRef = new VisualisResponseRefBuilder().setResponseBody(httpResult.getResponseBody()).build();
        checkResponseRef(responseRef);
        return responseRef;
    }

    public static void checkResponseRef(ResponseRef responseRef) throws ExternalOperationFailedException {
        if (responseRef.getStatus() != 0 && responseRef.getStatus() != 200) {
            logger.error(responseRef.getResponseBody());
            throw new ExternalOperationFailedException(90177, responseRef.getErrorMsg(), null);
        }
    }


    public static Long getNodeId(Map<String, Object> jobContent, String nodeName) {
        Object nodeIdObj = jobContent.get(nodeName);
        if (nodeIdObj == null) {
            return null;
        } else {
            return Long.parseLong(NumberUtils.parseDoubleString(nodeIdObj.toString()));
        }
    }
}
