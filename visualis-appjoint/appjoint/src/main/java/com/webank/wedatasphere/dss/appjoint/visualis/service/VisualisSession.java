/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appjoint.visualis.service;

import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shanhuang on 2019/10/12.
 */
public class VisualisSession implements Session {
    private long lastAccessTime;
    private Cookie[] cookies;
    private Header[] headers;
    private String user;
    private Map<String,String> parameters = new HashMap<>();

    @Override
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public Cookie[] getCookies() {
        return this.cookies;
    }

    @Override
    public Header[] getHeaders() {
        return this.headers;
    }

    @Override
    public Map<String,String> getParameters() {
        return this.parameters;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public void setCookies(Cookie[] cookies){
        this.cookies = cookies;
    }

    @Override
    public void updateLastAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }
}
