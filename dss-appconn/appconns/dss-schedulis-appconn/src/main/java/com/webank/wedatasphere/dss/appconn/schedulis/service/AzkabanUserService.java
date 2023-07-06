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

import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanUserEntity;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SchedulisHttpUtils;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class AzkabanUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzkabanUserService.class);

    private static List<AzkabanUserEntity> requestUserId(String releaseUser, String baseUrl, SSORequestOperation ssoRequestOperation, Workspace workspace) {
        LOGGER.info("try to update all releaseUsers from Schedulis url {}.", baseUrl);
        Map<String, Object> params = new HashMap<>(3);
        params.put("page", "1");
        params.put("pageSize", "100");
        params.put("serach", releaseUser);
        params.put("ajax", "loadSystemUserSelectData");
        String finalUrl = !baseUrl.endsWith("/") ? (baseUrl + "/" + "system") : baseUrl + "system";
        //systemUserTotalCount  page
        List<AzkabanUserEntity> newEntityList = new ArrayList<>();
        try {
            String response = SchedulisHttpUtils.getHttpGetResult(finalUrl, params, ssoRequestOperation, workspace);
            LOGGER.info("call schedulist method {}, response {}", finalUrl, response);
            Map<String, Object> map = DSSCommonUtils.COMMON_GSON.fromJson(response, Map.class);
            if (map.get("systemUserList") instanceof List) {
                newEntityList = ((List<Object>) map.get("systemUserList")).stream().map(e -> {
                            AzkabanUserEntity userEntity;
                            try {
                                userEntity = DSSCommonUtils.COMMON_GSON.fromJson(e.toString(), AzkabanUserEntity.class);
                            } catch (Exception ex) {
                                LOGGER.warn("AzkabanUserEntity: {} parsed from json failed!", e);
                                userEntity = null;
                            }
                            return userEntity;
                        }
                ).filter(Objects::nonNull).collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.error("update all releaseUsers from Schedulis url {} failed.", baseUrl, e);
        }
        return newEntityList;
    }

    public static boolean containsUser(String releaseUser, String baseUrl,
                                       SSORequestOperation ssoRequestOperation, Workspace workspace) {
        return requestUserId(releaseUser, baseUrl, ssoRequestOperation, workspace)
                .stream().anyMatch(userEntity -> userEntity.getUsername().equals(releaseUser));
    }

    public static String getUserId(String user, String baseUrl,
                                   SSORequestOperation ssoRequestOperation, Workspace workspace) {
        String userId = requestUserId(user, baseUrl, ssoRequestOperation, workspace)
                .stream().filter(userEntity -> userEntity.getUsername().equals(user)).findAny().orElse(new AzkabanUserEntity()).getId();
        if (StringUtils.isBlank(userId)) {
            throw new ExternalOperationFailedException(10823, "Not exists user in Schedulis " + user);
        }
        return userId;
    }
}
