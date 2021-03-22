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

package com.webank.wedatasphere.dss.appconn.visualis.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.linkis.httpclient.request.POSTAction;
import com.webank.wedatasphere.linkis.httpclient.request.UserAction;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisualisPostAction extends POSTAction implements UserAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(VisualisPostAction.class);
    String url;
    String user;

    @Override
    public String getRequestPayload() {
        try {
            return BDPJettyServerHelper.jacksonJson().writeValueAsString(getRequestPayloads());
        } catch (JsonProcessingException e) {
            LOGGER.error("failed to covert {} to a string", getRequestPayloads(), e);
            return "";
        }
    }

    @Override
    public String getURL() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getUser() {
        return user;
    }
}
