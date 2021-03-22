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

package com.webank.wedatasphere.dss.standard.sso.utils;

import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.SSOUrlBuilderOperationImpl;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * created by cooperyang on 2021/3/10
 * Description:
 */
public class SSOHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSOHelper.class);

    public final static CommonVars<String> GATEWAY_URL = CommonVars.apply("wds.linkis.gateway.url","");


    public static Workspace getWorkspace(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        Cookie workspaceCookie = Arrays.stream(cookies).
                filter(cookie -> "workspaceId".equals(cookie.getName())).findAny().orElse(null);
        Workspace workspace = new Workspace();
        if (workspaceCookie != null) {
            workspace.setWorkspaceName(workspaceCookie.getValue());
        }
        SSOUrlBuilderOperation ssoUrlBuilderOperation = new SSOUrlBuilderOperationImpl();
        Arrays.stream(cookies).forEach(cookie -> ssoUrlBuilderOperation.addCookie(cookie.getName(), cookie.getValue()));
        ssoUrlBuilderOperation.setDSSUrl(GATEWAY_URL.getValue());
        ssoUrlBuilderOperation.setWorkspace(workspace.getWorkspaceName());
        workspace.setSSOUrlBuilderOperation(ssoUrlBuilderOperation);
        return workspace;
    }

}
