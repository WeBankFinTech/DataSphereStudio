/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.standard.app.sso.builder.impl;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.SSOIntegrationConf;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by enjoyyin on 2020/8/6.
 */
public class SSOUrlBuilderOperationImpl implements SSOUrlBuilderOperation {

    private static final String SSO_URL_FORMAT = "%s?dssurl=%s&cookies=%s&workspace=%s&appName=%s";
    private static final String SSO_REDIRECT_URL_FORMAT = "%s?redirect=%s&dssurl=%s&cookies=%s&workspace=%s&appName=%s";
    private String workspaceName;
    private Map<String, String> cookies = new HashMap<>();
    private String dssUrl;
    private String redirectUrl;
    private String reqUrl;
    private String appName;

    protected String urlEncode(String str) throws AppStandardErrorException {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new AppStandardErrorException(60001, "Encode string failed! string: " + str, e);
        }
    }

    @Override
    public SSOUrlBuilderOperation setWorkspace(String workspaceName) {
        this.workspaceName = workspaceName;
        return this;
    }

    @Override
    public SSOUrlBuilderOperation addCookie(String key, String value) {
        cookies.put(key, value);
        return this;
    }

    @Override
    public SSOUrlBuilderOperation redirectTo(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    @Override
    public SSOUrlBuilderOperation setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
        return this;
    }

    @Override
    public SSOUrlBuilderOperation setDSSUrl(String dssUrl) {
        this.dssUrl = dssUrl;
        return this;
    }

    @Override
    public SSOUrlBuilderOperation setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    @Override
    public String getBuiltUrl() throws AppStandardErrorException {
        String cookieStr = cookies.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(";"));
        if(StringUtils.isNotBlank(redirectUrl)) {
            return String.format(SSO_REDIRECT_URL_FORMAT, reqUrl, redirectUrl, urlEncode(dssUrl),
                urlEncode(cookieStr), workspaceName, appName);
        } else {
            return String.format(SSO_URL_FORMAT, reqUrl, urlEncode(dssUrl),
                urlEncode(cookieStr), workspaceName, appName);
        }
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String getDssUrl() {
        return dssUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public String getAppName() {
        return appName;
    }

    @Override
    public SSOUrlBuilderOperation copy() {
        SSOUrlBuilderOperationImpl operation = new SSOUrlBuilderOperationImpl();
        operation.appName = appName;
        operation.cookies = cookies;
        operation.dssUrl = dssUrl;
        operation.redirectUrl = redirectUrl;
        operation.reqUrl = reqUrl;
        operation.workspaceName = workspaceName;
        return operation;
    }

    public static SSOUrlBuilderOperation restore(String operationStr){
        Map operationMap = SSOIntegrationConf.gson().fromJson(operationStr, Map.class);
        SSOUrlBuilderOperationImpl operation = new SSOUrlBuilderOperationImpl();
        if(operationMap.get("appName") != null) operation.appName = operationMap.get("appName").toString();
        if(operationMap.get("cookies") != null) operation.cookies = (Map<String, String>) operationMap.get("cookies");
        if(operationMap.get("dssUrl") != null) operation.dssUrl = operationMap.get("dssUrl").toString();
        if(operationMap.get("redirectUrl") != null) operation.redirectUrl = operationMap.get("redirectUrl").toString();
        if(operationMap.get("reqUrl") != null) operation.reqUrl = operationMap.get("reqUrl").toString();
        if(operationMap.get("workspaceName") != null) operation.workspaceName = operationMap.get("workspaceName").toString();
        return operation;
    }
}
