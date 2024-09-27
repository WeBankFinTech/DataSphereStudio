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

package com.webank.wedatasphere.dss.appconn.schedulis.utils;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Objects;

public class AzkabanUtils {

    public static String handleAzkabanEntity(String entityString) {
        if(StringUtils.isNotBlank(entityString)){
            if(entityString.startsWith("{") && entityString.endsWith("}")){
                Map resMap = DSSCommonUtils.COMMON_GSON.fromJson(entityString, Map.class);
                if(resMap.containsKey("error") && !Objects.isNull(resMap.get("error"))){
                    return (String)resMap.get("error");
                }
            }
            if(entityString.contains("<div class=\"container-full\">") && entityString.contains("<div class=\"login\">")){
                return "The SSO login status is invalid.Please refresh the browser and log in again!";
            }
        }

        Object object = DSSCommonUtils.COMMON_GSON.fromJson(entityString, Object.class);
        String status = null;
        String message = null;
        if (object instanceof Map) {
            Map map = (Map) object;
            if (map.get("status") != null) {
                status = map.get("status").toString();
            }
            if (StringUtils.isNotEmpty(status)) {
                if (null != map.get("message")) {
                    message = map.get("message").toString();
                }
            }
            if ("error".equalsIgnoreCase(status)) {
                return message;
            }
        }
        return "success";
    }
}
