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

package com.webank.wedatasphere.dss.appconn.visualis.utils;

import org.apache.linkis.httpclient.request.DownloadAction;
import org.apache.linkis.httpclient.request.GetAction;
import org.apache.linkis.httpclient.request.UserAction;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Function0;

import java.io.InputStream;

public class VisualisDownloadAction extends GetAction implements DownloadAction, UserAction {

    private String user;
    private String url;
    private InputStream inputStream;
    private HttpResponse response;


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


    @Override
    public HttpResponse getResponse() {
        return this.response;
    }

    @Override
    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    @Override
    public void write(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void write(InputStream inputStream, HttpResponse response) {
        write(inputStream);
        setResponse(response);
    }

    @Override
    public void trace(Function0<String> message) {
    }

    @Override
    public void debug(Function0<String> message) {
    }

    @Override
    public void info(Function0<String> message) {
    }

    @Override
    public void info(Function0<String> message, Throwable t) {
    }

    @Override
    public void warn(Function0<String> message) {
    }

    @Override
    public void warn(Function0<String> message, Throwable t) {
    }

    @Override
    public void error( Function0<String> message,  Throwable t) {
    }

    @Override
    public void error(Function0<String> message) {
    }

    @Override
    public Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }


}
