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

import static com.webank.wedatasphere.dss.appconn.schedulis.SchedulisAppConn.SCHEDULIS_APPCONN_NAME;

import com.webank.wedatasphere.dss.appconn.schedulis.Action.FlowScheduleAction;
import com.webank.wedatasphere.dss.appconn.schedulis.Action.FlowScheduleGetAction;
import com.webank.wedatasphere.dss.appconn.schedulis.Action.FlowScheduleUploadAction;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SSORequestWTSS {

    private final static Logger logger = LoggerFactory.getLogger(SSORequestWTSS.class);

    public static String requestWTSS(String url, String username, List<NameValuePair> params) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        Cookie cookie = null;
        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(cookie);
        HttpEntity entity = EntityBuilder.create().
                setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))
                .setParameters(params).build();
        httpPost.setEntity(entity);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            response = httpClient.execute(httpPost);
            HttpEntity ent = response.getEntity();
            String entStr = IOUtils.toString(ent.getContent(), "utf-8");
            return entStr;
        } catch (Exception e) {
            logger.error("requestWTSSError-->", e);
            throw e;
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
    }


    public static String requestWTSSWithSSOPost( String url,
                                                 Map<String,String> params,
                                                 AppIntegrationService<SSORequestService> service,
                                                 Workspace workspace
    ) throws Exception {

        try {

            FlowScheduleAction flowScheduleAction = new FlowScheduleAction();
            flowScheduleAction.getFormParams().putAll(params);
            SSOUrlBuilderOperation ssoUrlBuilderOperation = workspace.getSSOUrlBuilderOperation().copy();
            ssoUrlBuilderOperation.setAppName(SCHEDULIS_APPCONN_NAME);
            ssoUrlBuilderOperation.setReqUrl(url);
            ssoUrlBuilderOperation.setWorkspace(workspace.getWorkspaceName());
            flowScheduleAction.setURL(ssoUrlBuilderOperation.getBuiltUrl());
            SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation = service.getSSORequestService().createSSORequestOperation(SCHEDULIS_APPCONN_NAME);
            HttpResult previewResult = ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, flowScheduleAction);
            if (previewResult.getStatusCode() == 200 || previewResult.getStatusCode() == 0) {
                String response = previewResult.getResponseBody();
                return response;
            }else{
                throw new ExternalOperationFailedException(50063,"User sso request failed:"+url);
            }

        } catch (Exception e) {
            logger.error("requestWTSSPostError-->", e);
            throw e;
        }
    }

    public static String requestWTSSWithSSOGet( String url,
                                                 Map<String,Object> params,
                                                 SSORequestService service,
                                                 Workspace Workspace
    ) throws Exception {

        try {

            FlowScheduleGetAction flowScheduleGetAction = new FlowScheduleGetAction();
            flowScheduleGetAction.getParameters().putAll(params);
            SSOUrlBuilderOperation ssoUrlBuilderOperation =Workspace.getSSOUrlBuilderOperation().copy();
            ssoUrlBuilderOperation.setAppName(SCHEDULIS_APPCONN_NAME);
            ssoUrlBuilderOperation.setReqUrl(url);
            ssoUrlBuilderOperation.setWorkspace(Workspace.getWorkspaceName());
            flowScheduleGetAction.setURL(ssoUrlBuilderOperation.getBuiltUrl());
            SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation=service.createSSORequestOperation(SCHEDULIS_APPCONN_NAME);
            HttpResult previewResult = ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, flowScheduleGetAction);
            String response = previewResult.getResponseBody();
            return response;

        } catch (Exception e) {
            logger.error("requestWTSSGetError-->", e);
            throw e;
        }
    }


    public static HttpResult requestWTSSWithSSOUpload(String url,
                                                  FlowScheduleUploadAction uploadAction,
                                                  SSORequestService service,
                                                  Workspace Workspace) throws AppStandardErrorException {
        HttpResult previewResult= null;
        try {

            SSOUrlBuilderOperation ssoUrlBuilderOperation = Workspace.getSSOUrlBuilderOperation().copy();
            ssoUrlBuilderOperation.setAppName(SCHEDULIS_APPCONN_NAME);
            ssoUrlBuilderOperation.setReqUrl(url);
            ssoUrlBuilderOperation.setWorkspace(Workspace.getWorkspaceName());
            uploadAction.setURl(ssoUrlBuilderOperation.getBuiltUrl());
            SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation = service.createSSORequestOperation(SCHEDULIS_APPCONN_NAME);
            previewResult = ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, uploadAction);

        }catch (AppStandardErrorException e){
            logger.error("uploadWTSSGetError-->", e);
            throw e;
        }
        return previewResult;
    }
}
