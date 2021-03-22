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

package com.webank.wedatasphere.dss.appconn.schedulis.sso;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by cooperyang on 2021/1/13
 * Description:
 */
public class SchedulisPostRequestOperation implements SSORequestOperation<SchedulisHttpPost, CloseableHttpResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisPostRequestOperation.class);

    private SchedulisSecurityService schedulisSecurityService;


    private final Object LOCK = new Object();

    private CloseableHttpClient httpClient;

    public SchedulisPostRequestOperation(String baseUrl){
        super();
        this.schedulisSecurityService = SchedulisSecurityService.getInstance(baseUrl);
    }


    @Override
    public CloseableHttpResponse requestWithSSO(SSOUrlBuilderOperation urlBuilder, SchedulisHttpPost req) throws AppStandardErrorException {
        try {
            CookieStore cookieStore = new BasicCookieStore();
            cookieStore.addCookie(this.schedulisSecurityService.login(req.getUser()));
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            return httpClient.execute(req);
        } catch (Throwable t) {
            throw new AppStandardErrorException(90009, t.getMessage(), t);
        } finally {
            //do noting will change it
            //IOUtils.closeQuietly(httpClient);
        }
    }

    @Override
    public CloseableHttpResponse requestWithSSO(String url, SchedulisHttpPost req) throws AppStandardErrorException {
        return null;
    }
}
