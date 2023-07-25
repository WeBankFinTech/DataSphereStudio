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

import com.webank.wedatasphere.dss.appconn.schedulis.conf.AzkabanConf;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanUserEntity;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SchedulisHttpUtils;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AzkabanUserService {

    private static final Map<String, List<AzkabanUserEntity>> schedulisUserMap = new HashMap<>(2);
    private static final Map<String, Long> userMapLastModified = new ConcurrentHashMap<>(2);

    private static final Logger LOGGER = LoggerFactory.getLogger(AzkabanUserService.class);

    private static void requestUserId(String baseUrl, SSORequestOperation ssoRequestOperation, Workspace workspace) {
        if (userMapLastModified.containsKey(baseUrl) &&
                System.currentTimeMillis() - userMapLastModified.get(baseUrl) < AzkabanConf.REALESE_USER_FRESH_TIME.getValue().toLong()) {
            return;
        }
        LOGGER.info("try to update all releaseUsers from Schedulis url {}.", baseUrl);
        Map<String, Object> params = new HashMap<>(3);
        params.put("page", "1");
        params.put("pageSize", "10000");
        params.put("ajax", "loadSystemUserSelectData");
        String finalUrl = !baseUrl.endsWith("/") ? (baseUrl + "/" + "system") : baseUrl + "system";
        try {
            String response = SchedulisHttpUtils.getHttpGetResult(finalUrl, params, ssoRequestOperation, workspace);
            Map<String, Object> map = DSSCommonUtils.COMMON_GSON.fromJson(response, Map.class);
            if (map.get("systemUserList") instanceof List) {
                if (!schedulisUserMap.containsKey(baseUrl)) {
                    synchronized (schedulisUserMap) {
                        if (!schedulisUserMap.containsKey(baseUrl)) {
                            schedulisUserMap.put(baseUrl, new ArrayList<>());
                        }
                    }
                }
                List<AzkabanUserEntity> entityList = schedulisUserMap.get(baseUrl);
                List<AzkabanUserEntity> newEntityList = ((List<Object>) map.get("systemUserList")).stream().map(e -> {
                            AzkabanUserEntity userEntity;
                            try {
                                userEntity = DSSCommonUtils.COMMON_GSON.fromJson(e.toString(), AzkabanUserEntity.class);
                            } catch (Exception ex) {
                                LOGGER.warn("AzkabanUserEntity: {} parsed from json failed!", e.toString());
                                userEntity = null;
                            }
                            return userEntity;
                        }
                ).filter(Objects::nonNull).collect(Collectors.toList());
                synchronized (entityList) {
                    entityList.clear();
                    entityList.addAll(newEntityList);
                }
            }
        } catch (Exception e) {
            LOGGER.error("update all releaseUsers from Schedulis url {} failed.", baseUrl, e);
        }
        userMapLastModified.put(baseUrl, System.currentTimeMillis());
    }

    public static boolean containsUser(String releaseUser, String baseUrl,
                                       SSORequestOperation ssoRequestOperation, Workspace workspace) {
        Supplier<Boolean> supplier = () -> schedulisUserMap.containsKey(baseUrl) &&
                schedulisUserMap.get(baseUrl).stream().anyMatch(entity -> entity.getUsername().equals(releaseUser));
        if (!supplier.get()) {
            requestUserId(baseUrl, ssoRequestOperation, workspace);
        }
        return supplier.get();
    }

    public static String getUserId(String user, String baseUrl,
                                   SSORequestOperation ssoRequestOperation, Workspace workspace) {
        if (containsUser(user, baseUrl, ssoRequestOperation, workspace)) {
            return schedulisUserMap.get(baseUrl).stream().filter(entity -> entity.getUsername().equals(user)).findAny().get().getId();
        } else {
            throw new ExternalOperationFailedException(10823, "Not exists user in Schedulis " + user);
        }
    }
}
