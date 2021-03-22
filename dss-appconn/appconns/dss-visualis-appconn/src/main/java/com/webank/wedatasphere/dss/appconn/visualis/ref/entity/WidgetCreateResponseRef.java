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

package com.webank.wedatasphere.dss.appconn.visualis.ref.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.appconn.visualis.utils.NumberUtils;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

import java.util.Map;

public class WidgetCreateResponseRef extends AbstractResponseRef {

    String widgetId;

    public WidgetCreateResponseRef(String responseBody) throws Exception {
        super(responseBody, 0);
        responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
        status = NumberUtils.getInt(responseMap.get("status"));
        if (status != 0) {
            errorMsg = responseMap.get("message").toString();
        }
        widgetId = ((Map<String, Object>) responseMap.get("data")).get("widgetId").toString();
    }

    public String getWidgetId() {
        return widgetId;
    }

    @Override
    public Map<String, Object> toMap() {
        return responseMap;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

}
