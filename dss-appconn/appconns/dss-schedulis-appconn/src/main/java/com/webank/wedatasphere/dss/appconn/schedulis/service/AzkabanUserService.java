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

package com.webank.wedatasphere.dss.appconn.schedulis.service;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanUserEntity;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SSORequestWTSS;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AzkabanUserService {

    private static Map<String, String> schedulisUserMap = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(AzkabanUserService.class);
    private static final String ADMIN_USER = CommonVars.apply("wds.dss.schedulis.admin.user", "superadmin").getValue();

    private static void requestUserId(String baseUrl, SSORequestService ssoRequestService, Workspace workspace) {
            try {
                Map<String,Object> params = new HashMap<>();
                params.put("page", "1");
                params.put("pageSize", "10000");
                params.put("ajax","loadSystemUserSelectData");
                baseUrl = !baseUrl.endsWith("/") ? (baseUrl + "/") : baseUrl;
                String finalUrl = baseUrl + "system";
                LOGGER.info("Request User info from wtss url: "+finalUrl);
                String response = SSORequestWTSS.requestWTSSWithSSOGet(finalUrl,params,ssoRequestService,workspace);
                Map<String, Object> map = DSSCommonUtils.COMMON_GSON.fromJson(response, Map.class);
                if (map.get("systemUserList") instanceof List){
                    ((List<Object>) map.get("systemUserList")).forEach(e -> {
                        AzkabanUserEntity entity =  new Gson().fromJson(e.toString(), AzkabanUserEntity.class);
                        schedulisUserMap.put(entity.getUsername(),entity.getId());
                    });
                }
            } catch (Exception e) {
                LOGGER.error("get user from wtss failed。", e);
            }

    }

    public static String getUserIdByName(String username, String baseUrl, SSORequestService ssoRequestService, Workspace workspace) {
        if(schedulisUserMap.containsKey(username)) {
            return schedulisUserMap.get(username);
        }else{
            requestUserId(baseUrl,ssoRequestService,workspace);
            return schedulisUserMap.get(username);
        }

    }
}
