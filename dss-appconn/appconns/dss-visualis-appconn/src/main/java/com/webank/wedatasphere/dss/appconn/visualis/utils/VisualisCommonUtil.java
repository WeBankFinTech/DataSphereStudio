package com.webank.wedatasphere.dss.appconn.visualis.utils;

import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.enums.NodeNameEnum;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisDeleteAction;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisGetAction;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPostAction;
import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPutAction;
import com.webank.wedatasphere.dss.appconn.visualis.model.publish.VisualisCommonResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.*;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.SSOIntegrationConf;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.linkis.server.BDPJettyServerHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Description
 *
 * @Author elishazhang
 * @Date 2021/10/16
 */

public class VisualisCommonUtil {

    public static SSOUrlBuilderOperation getSSOUrlBuilderOperation(NodeRequestRef requestRef, String url) {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(VisualisAppConn.VISUALIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        return ssoUrlBuilderOperation;
    }

    public static SSOUrlBuilderOperation getSSOUrlBuilderOperation(ExportRequestRef requestRef, String url) {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(VisualisAppConn.VISUALIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        return ssoUrlBuilderOperation;
    }

    public static SSOUrlBuilderOperation getSSOUrlBuilderOperation(VisualisCopyRequestRef requestRef, String url) {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(VisualisAppConn.VISUALIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        return ssoUrlBuilderOperation;
    }

    public static SSOUrlBuilderOperation getSSOUrlBuilderOperation(ImportRequestRef requestRef, String url) {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(VisualisAppConn.VISUALIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        return ssoUrlBuilderOperation;
    }

    public static SSOUrlBuilderOperation getSSOUrlBuilderOperation(ProjectRequestRef projectRef, String url) {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = projectRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(VisualisAppConn.VISUALIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(projectRef.getWorkspace().getWorkspaceName());
        return ssoUrlBuilderOperation;
    }

    public static SSOUrlBuilderOperation getSSOUrlBuilderOperation(AsyncExecutionRequestRef requestRef, String url) {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = requestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(VisualisAppConn.VISUALIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        ssoUrlBuilderOperation.setWorkspace(requestRef.getWorkspace().getWorkspaceName());
        return ssoUrlBuilderOperation;
    }

    public static String getUser(AsyncExecutionRequestRef requestRef) {
        return requestRef.getExecutionRequestRefContext().getRuntimeMap().get("wds.dss.workflow.submit.user").toString();
    }

    /**
     * DSS创建visuallis节点，执行http创建请求，获取响应结果
     *
     * @param requestRef
     * @param ssoRequestOperation
     * @param url
     * @param visualisPostAction
     * @return
     * @throws ExternalOperationFailedException
     */
    public static VisualisCommonResponseRef getResponseRef(NodeRequestRef requestRef,
                                                           SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                                           String url,
                                                           VisualisPostAction visualisPostAction) throws ExternalOperationFailedException {

        VisualisCommonResponseRef responseRef;
        try {
            SSOUrlBuilderOperation ssoUrlBuilderOperation = getSSOUrlBuilderOperation(requestRef, url);
            visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
            responseRef = new VisualisCommonResponseRef(httpResult.getResponseBody());
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Create visualis node Exception", e);
        }
        return responseRef;
    }

    /**
     * visualis工程查询并返回结果
     *
     * @param httpResult
     * @return
     * @throws ExternalOperationFailedException
     */
    public static Map getResponseMap(HttpResult httpResult) throws ExternalOperationFailedException {
        try {
            return BDPJettyServerHelper.jacksonJson().readValue(httpResult.getResponseBody(), Map.class);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "search visualis project exception", e);
        }
    }



    /**
     * 检查响应结果是否有效
     *
     * @param resMap
     * @throws ExternalOperationFailedException
     */
    public static void checkResponseMap(Map resMap) throws ExternalOperationFailedException {
        @SuppressWarnings("unchecked")
        Map<String, Object> header = (Map<String, Object>) resMap.get("header");
        int code = Integer.parseInt(NumberUtils.parseDoubleString(header.get("code").toString()));
        if (code != 200) {
            String errorMsg = header.toString();
            throw new ExternalOperationFailedException(90177, errorMsg, null);
        }
    }

    /**
     * DSS删除visuallis节点，执行http删除请求，获取响应结果
     *
     * @param visualisDeleteRequestRef
     * @param ssoRequestOperation
     * @param url
     * @param deleteAction
     * @return
     * @throws ExternalOperationFailedException
     */
    public static Map getResponseMap(NodeRequestRef visualisDeleteRequestRef, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String url, VisualisDeleteAction deleteAction) throws ExternalOperationFailedException {
        Map resMap;
        try {
            SSOUrlBuilderOperation ssoUrlBuilderOperation = VisualisCommonUtil.getSSOUrlBuilderOperation(visualisDeleteRequestRef, url);
            deleteAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, deleteAction);
            String responseBody = httpResult.getResponseBody();
            resMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Delete visualis node Exception", e);
        }
        return resMap;
    }

    /**
     * DSS更新visuallis节点，执行http put请求，获取响应结果
     *
     * @param visualisUpdateRequestRef
     * @param ssoRequestOperation
     * @param url
     * @param putAction
     * @return
     * @throws ExternalOperationFailedException
     */
    public static Map getResponseMap(NodeRequestRef visualisUpdateRequestRef, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String url, VisualisPutAction putAction) throws ExternalOperationFailedException {
        Map resMap;
        try {
            SSOUrlBuilderOperation ssoUrlBuilderOperation = VisualisCommonUtil.getSSOUrlBuilderOperation(visualisUpdateRequestRef, url);
            putAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, putAction);
            resMap = BDPJettyServerHelper.jacksonJson().readValue(httpResult.getResponseBody(), Map.class);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Update visualis node Exception", e);
        }
        return resMap;
    }

    /**
     * DSS更新visuallis节点，执行http post请求，获取响应结果
     *
     * @param visualisUpdateRequestRef
     * @param ssoRequestOperation
     * @param url
     * @param postAction
     * @return
     * @throws ExternalOperationFailedException
     */
    public static Map getResponseMap(NodeRequestRef visualisUpdateRequestRef, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String url, VisualisPostAction postAction) throws ExternalOperationFailedException {
        Map resMap;
        try {
            SSOUrlBuilderOperation ssoUrlBuilderOperation = VisualisCommonUtil.getSSOUrlBuilderOperation(visualisUpdateRequestRef, url);
            postAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, postAction);
            resMap = BDPJettyServerHelper.jacksonJson().readValue(httpResult.getResponseBody(), Map.class);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Update visualis node Exception", e);
        }
        return resMap;
    }

    /**
     * DSS复制visuallis节点，执行http post请求，获取响应结果
     *
     * @param requestRef
     * @param jobContent
     * @param url
     * @param visualisPostAction
     * @param ssoRequestOperation
     * @param NodeName
     * @return
     * @throws ExternalOperationFailedException
     */
    public static VisualisCopyResponseRef getResponseRef(VisualisCopyRequestRef requestRef,
                                                         Map<String, Object> jobContent,
                                                         String url,
                                                         VisualisPostAction visualisPostAction,
                                                         SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                                         String NodeName) throws ExternalOperationFailedException {
        VisualisCopyResponseRef responseRef;
        try {
            HttpResult httpResult = getHttpResult(requestRef, url, visualisPostAction, ssoRequestOperation);
            String responseBody = httpResult.getResponseBody();
            if (httpResult.getStatusCode() != 200) {
                throw new ExternalOperationFailedException(90175, "copy " + NodeName + "  Exception" + httpResult.getResponseBody());
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = SSOIntegrationConf.gson().fromJson(responseBody, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
            String id;
            switch (NodeNameEnum.getEnum(NodeName)) {

                case DISPLAY_NAME:
                    id = NumberUtils.parseDoubleString(jobContent.get("displayId").toString());
                    @SuppressWarnings("unchecked")
                    Map<String, Object> displayData = (Map<String, Object>) dataMap.get(NodeName);
                    jobContent.put("displayId", Double.parseDouble(displayData.get(id).toString()));
                    break;
                case DASHBOARD_NAME:
                    //dashboardId
                    String dashboardId = NumberUtils.parseDoubleString(jobContent.get("id").toString());
                    Map<String, Object> dashboardData = (Map<String, Object>) dataMap.get("dashboard");
                    jobContent.put("id", Double.parseDouble(dashboardData.get(dashboardId).toString()));
                    @SuppressWarnings("unchecked")

                    //dashboardPortal
                            String dashboardPortalId = NumberUtils.parseDoubleString(jobContent.get("dashboardPortalId").toString());
                    Map<String, Object> dashboardPortalData = (Map<String, Object>) dataMap.get("dashboardPortal");
                    jobContent.put("dashboardPortalId", Double.parseDouble(dashboardPortalData.get(dashboardPortalId).toString()));
                    break;
                case VIEW_NAME:
                    id = NumberUtils.parseDoubleString(jobContent.get("id").toString());
                    @SuppressWarnings("unchecked")
                    Map<String, Object> viewData = (Map<String, Object>) dataMap.get(NodeName);
                    jobContent.put("id", Double.parseDouble(viewData.get(id).toString()));
                    break;
                default:
                    throw new ExternalOperationFailedException(90176, "get id failed when copy " + NodeName + " node");
            }
            responseRef = new VisualisCopyResponseRef(jobContent, responseBody);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "copy " + NodeName + " node Exception", e);
        }
        return responseRef;
    }

    /**
     * DSS导出visuallis节点，执行http post请求，获取响应结果
     *
     * @param visualisPostAction
     * @param url
     * @param requestRef
     * @param ssoRequestOperation
     * @return
     * @throws ExternalOperationFailedException
     */
    public static ResponseRef getResponseRef(VisualisPostAction visualisPostAction, String url, ImportRequestRef requestRef, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException {
        ResponseRef responseRef;
        try {
            HttpResult httpResult = getHttpResult(visualisPostAction, url, requestRef, ssoRequestOperation);
            responseRef = new VisualisImportResponseRef((Map<String, Object>) requestRef.getParameter("jobContent"), httpResult.getResponseBody(), requestRef.getParameter("nodeType").toString(), requestRef.getParameter("projectId"));
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "Import Visualis node Exception", e);
        }
        return responseRef;
    }

    /**
     * DSS导出widget节点，执行http post请求，获取响应结果
     *
     * @param visualisPostAction
     * @param url
     * @param requestRef
     * @param ssoRequestOperation
     * @param nodeName
     * @return
     * @throws ExternalOperationFailedException
     */
    public static ResponseRef getResponseRef(VisualisPostAction visualisPostAction, String url, ImportRequestRef requestRef, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String nodeName) throws ExternalOperationFailedException {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = VisualisCommonUtil.getSSOUrlBuilderOperation(requestRef, url);
        ResponseRef responseRef;
        try {
            visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            HttpResult httpResult = ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
            if (httpResult.getStatusCode() != 200) {
                throw new ExternalOperationFailedException(90175, "import Visualis Exception" + httpResult.getResponseBody());
            }
            responseRef = new VisualisImportResponseRef((Map<String, Object>) requestRef.getParameter("jobContent"), httpResult.getResponseBody(), requestRef.getParameter("nodeType").toString(), requestRef.getParameter("projectId"));
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90176, "import Visualis Exception", e);
        }
        return responseRef;
    }

    /**
     * DSS查询widget节点，执行http请求，获取响应结果
     *
     * @param visualisOpenRequestRef
     * @param projectId
     * @param baseUrl
     * @param jumpUrlFormat
     * @param id
     * @return
     */
    public static ResponseRef getResponseRef(VisualisOpenRequestRef visualisOpenRequestRef, Long projectId, String baseUrl, String jumpUrlFormat, String id) {
        String jumpUrl = URLUtils.getUrl(baseUrl, jumpUrlFormat, projectId.toString(), id, visualisOpenRequestRef.getName());
        String retJumpUrl = URLUtils.getEnvUrl(jumpUrl, visualisOpenRequestRef);
        Map<String, String> retMap = new HashMap<>();
        retMap.put("jumpUrl", retJumpUrl);
        return new VisualisOpenResponseRef(DSSCommonUtils.COMMON_GSON.toJson(retMap), 0);
    }

    public static HttpResult getHttpResult(ExportRequestRef requestRef, String url, VisualisPostAction visualisPostAction, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws AppStandardErrorException {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = VisualisCommonUtil.getSSOUrlBuilderOperation(requestRef, url);
        visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
        return ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
    }

    public static HttpResult getHttpResult(VisualisCopyRequestRef requestRef, String url, VisualisPostAction visualisPostAction, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws AppStandardErrorException {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = VisualisCommonUtil.getSSOUrlBuilderOperation(requestRef, url);
        visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
        return ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
    }

    public static HttpResult getHttpResult(AsyncExecutionRequestRef ref, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String url, VisualisDownloadAction visualisDownloadAction) throws AppStandardErrorException {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = VisualisCommonUtil.getSSOUrlBuilderOperation(ref, url);
        visualisDownloadAction.setURL(ssoUrlBuilderOperation.getBuiltUrl());
        return ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisDownloadAction);
    }

    public static HttpResult getHttpResult(AsyncExecutionRequestRef ref, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String url, VisualisPostAction visualisPostAction) throws AppStandardErrorException {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = VisualisCommonUtil.getSSOUrlBuilderOperation(ref, url);
        visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
        return ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
    }

    public static HttpResult getHttpResult(VisualisPostAction visualisPostAction, String url, ImportRequestRef requestRef, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws AppStandardErrorException {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = VisualisCommonUtil.getSSOUrlBuilderOperation(requestRef, url);
        visualisPostAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
        return ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisPostAction);
    }

    public static HttpResult getHttpResult(VisualisGetAction visualisGetAction, String url, ProjectRequestRef requestRef, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws AppStandardErrorException {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = getSSOUrlBuilderOperation(requestRef, url);
        visualisGetAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
        return ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, visualisGetAction);
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
