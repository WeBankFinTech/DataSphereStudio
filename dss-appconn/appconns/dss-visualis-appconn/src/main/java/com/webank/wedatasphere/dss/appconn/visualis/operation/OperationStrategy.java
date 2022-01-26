package com.webank.wedatasphere.dss.appconn.visualis.operation;

import com.webank.wedatasphere.dss.appconn.visualis.model.VisualisPostAction;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisCopyRequestRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisOpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.ref.*;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;

import java.util.Map;


public interface OperationStrategy {

    ResponseRef createRef(NodeRequestRef requestRef,
                          String baseUrl,
                          DevelopmentService developmentService,
                          SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException;

    void deleteRef(String baseUrl,
                   NodeRequestRef visualisDeleteRequestRef,
                   SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException;

    ResponseRef exportRef(ExportRequestRef requestRef,
                          String url,
                          VisualisPostAction visualisPostAction,
                          String externalContent,
                          SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws Exception;

    ResponseRef query(VisualisOpenRequestRef visualisOpenRequestRef, String externalContent, Long projectId, String baseUrl) throws Exception;

    ResponseRef updateRef(UpdateRequestRef requestRef,
                          NodeRequestRef visualisUpdateRequestRef,
                          String baseUrl,
                          SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException;

    ResponseRef copyRef(VisualisCopyRequestRef requestRef,
                        Map<String, Object> jobContent,
                        String url,
                        String nodeType,
                        VisualisPostAction visualisPostAction,
                        SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException;

    ResponseRef importRef(VisualisPostAction visualisPostAction,
                          String url,
                          ImportRequestRef requestRef,
                          SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                          DevelopmentService developmentService) throws ExternalOperationFailedException;

    ResponseRef executeRef(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException;

    String getId(AsyncExecutionRequestRef requestRef);

    String submit(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) throws ExternalOperationFailedException;

    RefExecutionState state(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String execId) throws ExternalOperationFailedException;

    ResponseRef getAsyncResult(AsyncExecutionRequestRef ref, String baseUrl, SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation, String execId) throws ExternalOperationFailedException;

}
