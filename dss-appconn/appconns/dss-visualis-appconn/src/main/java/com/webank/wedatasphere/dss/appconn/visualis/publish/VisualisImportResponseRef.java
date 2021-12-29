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

package com.webank.wedatasphere.dss.appconn.visualis.publish;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.standard.app.development.ref.DSSCommonResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

import java.util.Map;

public class VisualisImportResponseRef extends DSSCommonResponseRef {

    Map<String, Object> importedMap = Maps.newHashMap();
    Map<String, Object> newJobContent = Maps.newHashMap();

    public VisualisImportResponseRef(Map<String, Object> jobContent, String responseBody, String nodeType, Object projectId) throws Exception {
        super(responseBody);

        if("linkis.appconn.visualis.widget".equalsIgnoreCase(nodeType)){
            Map<String, Object> payload = (Map<String, Object>) jobContent.get("data");
            Long id = ((Double) Double.parseDouble(payload.get("widgetId").toString())).longValue();
            payload.put("widgetId", ((Double) ((Map<String, Integer>) ((Map<String, Object>) responseMap.get("data")).get("widget")).get(id.toString()).doubleValue()).toString());
        } else if("linkis.appconn.visualis.display".equalsIgnoreCase(nodeType)){
            Map<String, Object> payload = (Map<String, Object>) jobContent.get("payload");
            Long id = ((Double) Double.parseDouble(payload.get("id").toString())).longValue();
            payload.put("projectId", projectId);
            payload.put("id", ((Double) ((Map<String, Integer>) ((Map<String, Object>) responseMap.get("data")).get("display")).get(id.toString()).doubleValue()).toString());
        } else if("linkis.appconn.visualis.dashboard".equalsIgnoreCase(nodeType)){
            Map<String, Object> payload = (Map<String, Object>) jobContent.get("payload");
            Long id = ((Double) Double.parseDouble(payload.get("id").toString())).longValue();
            payload.put("projectId", projectId);
            payload.put("id", ((Double) ((Map<String, Integer>) ((Map<String, Object>) responseMap.get("data")).get("dashboardPortal")).get(id.toString()).doubleValue()).toString());
        } else {
            throw new ExternalOperationFailedException(90177, "Unknown task type " + nodeType, null);
        }
        this.newJobContent = jobContent;
    }

    @Override
    public Map<String, Object> toMap() {
        return newJobContent;
    }

}
