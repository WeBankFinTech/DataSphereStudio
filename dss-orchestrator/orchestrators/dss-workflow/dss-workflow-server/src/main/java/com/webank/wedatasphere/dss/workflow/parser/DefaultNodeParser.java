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

package com.webank.wedatasphere.dss.workflow.parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.workflow.common.parser.NodeParser;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DefaultNodeParser implements NodeParser {
    @Override
    public String updateNodeResource(String nodeJson, List<Resource> resources) throws IOException {
        if(resources.size() ==0){
            return nodeJson;
        }
        Map<String, Object> nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(nodeJson, Map.class);
        nodeJsonMap.replace("resources",resources);

        return BDPJettyServerHelper.jacksonJson().writeValueAsString(nodeJsonMap);
    }

    @Override
    public String updateNodeJobContent(String nodeJson, Map<String,Object> nodeExportContent) throws IOException {
        if(nodeExportContent ==null){
            return nodeJson;
        }
        Map<String, Object> nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(nodeJson, Map.class);
        nodeJsonMap.replace("jobContent",nodeExportContent);
        return BDPJettyServerHelper.jacksonJson().writeValueAsString(nodeJsonMap);
    }

    @Override
    public String updateSubFlowID(String nodeJson, long subflowId) throws IOException {
        Map<String, Object> nodeContentMap = new HashMap<>();
        nodeContentMap.put("embeddedFlowId",subflowId);
        return updateNodeJobContent(nodeJson,nodeContentMap);
    }

    @Override
    public String getNodeValue(String key, String nodeJson) throws IOException {
        if(StringUtils.isEmpty(nodeJson)){
            return null;
        }
        Map<String, Object> nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(nodeJson, Map.class);
        return nodeJsonMap.get(key).toString();

    }


    @Override
    public Map<String, Object> getNodeJobContent(String nodeJson) throws IOException {
        Map<String, Object> nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(nodeJson, Map.class);
        return  (LinkedHashMap<String, Object>) nodeJsonMap.get("jobContent");
    }


    @Override
    public List<Resource> getNodeResource(String nodeJson) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(nodeJson).getAsJsonObject();
        JsonArray resourcesJsonArray = jsonObject.getAsJsonArray("resources");
        List<Resource> resources = gson.fromJson(resourcesJsonArray, new com.google.gson.reflect.TypeToken<List<Resource>>() {
        }.getType());
        return resources;
    }
}
