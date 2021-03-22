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

import com.webank.wedatasphere.dss.appconn.visualis.utils.NumberUtils;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

import java.util.Map;

public class DashboardCreateResponseRef extends AbstractResponseRef {

    String dashboardPortalId;

    public DashboardCreateResponseRef(String responseBody) throws Exception {
        super(responseBody, 200);
        responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
        Map<String, Object> header = (Map<String, Object>) responseMap.get("header");
        status = NumberUtils.getInt(header.get("code"));
        if (status != 200) {
            errorMsg = header.toString();
        }
        dashboardPortalId = ((Map<String, Object>) responseMap.get("payload")).get("id").toString();
    }

    public String getDashboardPortalId() {
        return dashboardPortalId;
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
