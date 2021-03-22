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

import com.webank.wedatasphere.dss.standard.app.sso.builder.DssMsgBuilderOperation.DSSMsg;
import java.util.Map;

/**
 * Created by enjoyyin on 2020/8/7.
 */
public class DSSMsgImpl implements DSSMsg {

    private String redirectUrl;
    private String workspaceName;
    private String dssUrl;
    private String appName;
    private Map<String, String> cookies;

    @Override
    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Override
    public String getWorkspaceName() {
        return workspaceName;
    }

    @Override
    public String getDSSUrl() {
        return dssUrl;
    }

    @Override
    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public void setDSSUrl(String dssUrl) {
        this.dssUrl = dssUrl;
    }

    @Override
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }
}
