/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.schedulis.utils;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSGetAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSHttpAction;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.webank.wedatasphere.dss.appconn.schedulis.SchedulisAppConn.SCHEDULIS_APPCONN_NAME;


public class SchedulisHttpUtils {

    private final static Logger logger = LoggerFactory.getLogger(SchedulisHttpUtils.class);

    public static SSOUrlBuilderOperation getSSORequestOperation(String url, Workspace workspace) {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = SSOHelper.createSSOUrlBuilderOperation(workspace);
        ssoUrlBuilderOperation.setAppName(SCHEDULIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        return ssoUrlBuilderOperation;
    }

    public static String getHttpResult(String url,
                                       DSSHttpAction action,
                                       SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                       Workspace workspace) {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = getSSORequestOperation(url, workspace);
        action.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
        HttpResult previewResult = ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, action);
        if (previewResult.getStatusCode() == 200 || previewResult.getStatusCode() == 0) {
            logger.info("request Schedulis success, responseBody is {}", DSSCommonUtils.COMMON_GSON.toJson(previewResult));
            return previewResult.getResponseBody();
        } else {
            logger.error("request Schedulis failed, responseBody is {}.", previewResult.getResponseBody());
            throw new ExternalOperationFailedException(50063, "request Schedulis failed." + previewResult.getResponseBody());
        }
    }

    public static String getHttpGetResult(String url,
                                          Map<String, Object> params,
                                          SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                          Workspace workspace) {
        DSSGetAction getAction = new DSSGetAction();
        getAction.getParameters().putAll(params);
        return getHttpResult(url, getAction, ssoRequestOperation, workspace);
    }

    public static String getHttpPostResult(String url,
                                          Map<String, String> params,
                                          SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                          Workspace workspace) {
        DSSPostAction getAction = new DSSPostAction();
        getAction.getFormParams().putAll(params);
        return getHttpResult(url, getAction, ssoRequestOperation, workspace);
    }

    public static String getHttpPostResult(String url,
                                           Map<String, String> params,
                                           Map<String, Object> payloads,
                                           SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                           Workspace workspace) {
        DSSPostAction getAction = new DSSPostAction();
        getAction.getFormParams().putAll(params);
        getAction.getRequestPayloads().putAll(payloads);
        return getHttpResult(url, getAction, ssoRequestOperation, workspace);
    }

}
