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

package com.webank.wedatasphere.dss.standard.app.sso.builder;


import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.dss.standard.common.service.Operation;

/**
 * Created by enjoyyin on 2020/8/5.
 */
public interface SSOUrlBuilderOperation extends Operation {

    SSOUrlBuilderOperation setReqUrl(String reqUrl);

    SSOUrlBuilderOperation setWorkspace(String workspaceName);

    SSOUrlBuilderOperation setDSSUrl(String dssUrl);

    SSOUrlBuilderOperation setAppName(String appConnName);

    SSOUrlBuilderOperation addCookie(String key, String value);

    SSOUrlBuilderOperation redirectTo(String redirectUrl);

    SSOUrlBuilderOperation copy();

    String getBuiltUrl() throws AppStandardErrorException;
}
