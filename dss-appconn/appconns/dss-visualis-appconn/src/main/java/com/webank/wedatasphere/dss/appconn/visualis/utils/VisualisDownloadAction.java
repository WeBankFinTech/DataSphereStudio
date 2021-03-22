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

package com.webank.wedatasphere.dss.appconn.visualis.utils;

import com.webank.wedatasphere.linkis.httpclient.request.DownloadAction;
import com.webank.wedatasphere.linkis.httpclient.request.GetAction;
import com.webank.wedatasphere.linkis.httpclient.request.UserAction;

import java.io.InputStream;

public class VisualisDownloadAction extends GetAction implements DownloadAction, UserAction {

    private String user;
    private String url;
    private InputStream inputStream;

    @Override
    public void write(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    public void setURL(String url) {
        this.url = url;
    }

    @Override
    public String getURL() {
        return url;
    }
}
