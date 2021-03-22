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

package com.webank.wedatasphere.dss.appconn.visualis.publish;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

import java.util.Map;

public class VisualisExportResponseRef extends AbstractResponseRef {

    Map<String, Object> bmlResource;

    public VisualisExportResponseRef(String responseBody) throws Exception {
        super(responseBody, 0);
        responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
        status = (int) responseMap.get("status");
        if (status != 0) {
            errorMsg = responseMap.get("message").toString();
        }
        bmlResource = ((Map<String, Object>) responseMap.get("data"));
    }

    @Override
    public Map<String, Object> toMap() {
        return bmlResource;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }
}
