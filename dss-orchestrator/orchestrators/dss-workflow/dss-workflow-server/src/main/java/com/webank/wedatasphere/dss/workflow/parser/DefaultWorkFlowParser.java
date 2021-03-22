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

package com.webank.wedatasphere.dss.workflow.parser;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/03/04 03:59 PM
 */
@Component
public class DefaultWorkFlowParser implements WorkFlowParser {
    @Override
    public List<Resource> getWorkFlowResources(String workFlowJson) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(workFlowJson).getAsJsonObject();
        JsonArray resourcesJsonArray = jsonObject.getAsJsonArray("resources");
        List<Resource> resources = gson.fromJson(resourcesJsonArray, new com.google.gson.reflect.TypeToken<List<Resource>>() {
        }.getType());
        return resources;
    }

    @Override
    public List<DSSNode> getWorkFlowNodes(String workFlowJson) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(workFlowJson).getAsJsonObject();
        JsonArray nodeJsonArray = jsonObject.getAsJsonArray("nodes");
        List<DSSNode> dwsNodes = gson.fromJson(nodeJsonArray, new TypeToken<List<DSSNodeDefault>>() {
        }.getType());
        return dwsNodes;
    }

    @Override
    public List<String> getWorkFlowNodesJson(String workFlowJson) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(workFlowJson).getAsJsonObject();
        JsonArray nodeJsonArray = jsonObject.getAsJsonArray("nodes");
        if(nodeJsonArray==null){
            return null;
        }
        List<Object> nodeJsonList = gson.fromJson(nodeJsonArray.toString(), new TypeToken<List<Object>>() {
        }.getType());
        return nodeJsonList.stream().map(gson::toJson).collect(Collectors.toList());
    }

    @Override
    public String updateFlowJsonWithKey(String workFlowJson, String key, Object value) throws IOException {
        if(value == null || key == null){
            return workFlowJson;
        }
        Map<String, Object> flowJsonObject = BDPJettyServerHelper.jacksonJson().readValue(workFlowJson, Map.class);


        flowJsonObject.replace(key,value);
        String updatedJson = BDPJettyServerHelper.jacksonJson().writeValueAsString(flowJsonObject);
        return updatedJson;
    }

    @Override
    public String getValueWithKey(String workFlowJson, String key) throws IOException {
        if(key == null){
            return null;
        }
        Map<String, Object> flowJsonObject = BDPJettyServerHelper.jacksonJson().readValue(workFlowJson, Map.class);


        return flowJsonObject.get(key).toString();

    }
}
