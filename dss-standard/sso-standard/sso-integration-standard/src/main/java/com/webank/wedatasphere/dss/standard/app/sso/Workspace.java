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

package com.webank.wedatasphere.dss.standard.app.sso;

import com.webank.wedatasphere.dss.common.entity.DSSWorkspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import org.apache.linkis.common.conf.Configuration;

import java.util.HashMap;
import java.util.Map;


public class Workspace implements DSSWorkspace {

    protected long workspaceId;
    protected String workspaceName;
    protected Map<String, String> cookies = new HashMap<>();
    protected String dssUrl = Configuration.GATEWAY_URL().getValue();

    @Override
    public long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(long workspaceId) {
        this.workspaceId = workspaceId;
    }

    @Override
    public String getWorkspaceName() {
        return this.workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getDssUrl() {
        return dssUrl;
    }

    public void setDssUrl(String dssUrl) {
        this.dssUrl = dssUrl;
    }

    @Override
    public String toString() {
        return "Workspace{" +
                "workspaceId=" + workspaceId +
                ", workspaceName='" + workspaceName + '\'' +
                ", cookies=" + cookies +
                ", dssUrl='" + dssUrl + '\'' +
                '}';
    }
}