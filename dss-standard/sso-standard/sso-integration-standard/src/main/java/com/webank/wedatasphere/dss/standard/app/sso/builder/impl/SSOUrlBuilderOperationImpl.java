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

package com.webank.wedatasphere.dss.standard.app.sso.builder.impl;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class SSOUrlBuilderOperationImpl implements SSOUrlBuilderOperation {

    private static final String SSO_URL_FORMAT = "%s?dssurl=%s&cookies=%s&workspace=%s&appName=%s";
    private static final String SSO_REDIRECT_URL_FORMAT = "%s?redirect=%s&dssurl=%s&cookies=%s&workspace=%s&appName=%s";
    private String workspaceName;
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> queryParameters = new HashMap<>();
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
    public SSOUrlBuilderOperation addQueryParameter(String key, String value) {
        queryParameters.put(key, value);
        return this;
    }

    @Override
    public String getBuiltUrl() throws AppStandardErrorException {
        String cookieStr = cookies.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(";"));
        String url;
        if(StringUtils.isNotBlank(redirectUrl)) {
            url = String.format(SSO_REDIRECT_URL_FORMAT, reqUrl, redirectUrl, urlEncode(dssUrl),
                urlEncode(cookieStr), urlEncode(workspaceName), appName);
        } else {
            url = String.format(SSO_URL_FORMAT, reqUrl, urlEncode(dssUrl),
                urlEncode(cookieStr), urlEncode(workspaceName), appName);
        }
        if(queryParameters.isEmpty()) {
            return url;
        } else {
            String queryParameterStr = queryParameters.entrySet().stream().map(entry -> entry.getKey() + "=" + urlEncode(entry.getValue()))
                    .collect(Collectors.joining("&"));
            return url + "&" + queryParameterStr;
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

}
