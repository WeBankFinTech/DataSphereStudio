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
            Map<String, Object> tempDataMap = (Map<String, Object>) responseMap.get("data");
            Map<String, Object> tempValueMap = (Map<String, Object>) tempDataMap.get("widget");
            payload.put("widgetId", getIdByMap(tempValueMap, id));
        } else if("linkis.appconn.visualis.display".equalsIgnoreCase(nodeType)){
            Map<String, Object> payload = (Map<String, Object>) jobContent.get("payload");
            Long id = ((Double) Double.parseDouble(payload.get("id").toString())).longValue();
            payload.put("projectId", projectId);
            Map<String, Object> tempDataMap = (Map<String, Object>) responseMap.get("data");
            Map<String, Object> tempValueMap = (Map<String, Object>) tempDataMap.get("display");
            payload.put("id", getIdByMap(tempValueMap, id));
        } else if("linkis.appconn.visualis.dashboard".equalsIgnoreCase(nodeType)){
            Map<String, Object> payload = (Map<String, Object>) jobContent.get("payload");
            Long id = ((Double) Double.parseDouble(payload.get("id").toString())).longValue();
            payload.put("projectId", projectId);
            Map<String, Object> tempDataMap = (Map<String, Object>) responseMap.get("data");
            Map<String, Object> tempValueMap = (Map<String, Object>) tempDataMap.get("dashboardPortal");
            payload.put("id", getIdByMap(tempValueMap, id));
        } else {
            throw new ExternalOperationFailedException(90177, "Unknown task type " + nodeType, null);
        }
        this.newJobContent = jobContent;
    }

    /**
     * 获取对应的Id
     * @param tempValueMap
     * @param id
     * @return
     */
    public String getIdByMap(Map<String, Object> tempValueMap, Long id) {
        Object tempObjectVal = tempValueMap.get(id.toString());
        if (tempObjectVal instanceof Double) {
            Double t = ((Double) tempObjectVal).doubleValue();
            return t.toString();
        } else if (tempObjectVal instanceof Integer) {
            Integer t = (Integer) tempObjectVal;
            return t.toString();
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return newJobContent;
    }
}
