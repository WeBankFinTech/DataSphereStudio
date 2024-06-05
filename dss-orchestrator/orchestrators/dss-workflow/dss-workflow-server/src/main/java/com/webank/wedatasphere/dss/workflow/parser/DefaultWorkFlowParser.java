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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.node.DSSEdge;
import com.webank.wedatasphere.dss.common.entity.node.DSSEdgeDefault;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DefaultWorkFlowParser implements WorkFlowParser {
    @Override
    public List<Resource> getWorkFlowResources(String workFlowJson) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(workFlowJson).getAsJsonObject();
        JsonArray resourcesJsonArray = jsonObject.getAsJsonArray("resources");
        List<Resource> resources = DSSCommonUtils.COMMON_GSON.fromJson(resourcesJsonArray, new com.google.gson.reflect.TypeToken<List<Resource>>() {
        }.getType());
        return resources;
    }

    @Override
    public List<String> getParamConfTemplate(String workFlowJson) {
        List<DSSNode> nodes = getWorkFlowNodes(workFlowJson);
        return nodes.stream()
                .filter(e ->e.getParams()!=null&& e.getParams().containsKey("configuration")
                        &&((Map<String,Object>)e.getParams().get("configuration")).containsKey("startup"))
                .map(e -> (Map<String,Object>) ((Map<String,Object>)e.getParams().get("configuration")).get("startup"))
                .filter(e -> e.containsKey("ec.conf.templateId"))
                .map(e -> (String) e.get("ec.conf.templateId"))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<DSSNode> getWorkFlowNodes(String workFlowJson) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(workFlowJson).getAsJsonObject();
        JsonArray nodeJsonArray = jsonObject.getAsJsonArray("nodes");
        List<DSSNode> dwsNodes = DSSCommonUtils.COMMON_GSON.fromJson(nodeJsonArray, new TypeToken<List<DSSNodeDefault>>() {
        }.getType());
        return dwsNodes;
    }

    @Override
    public List<DSSEdge> getWorkFlowEdges(String workFlowJson) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(workFlowJson).getAsJsonObject();
        JsonArray edgeJsonArray = jsonObject.getAsJsonArray("edges");
        List<DSSEdge> edges = DSSCommonUtils.COMMON_GSON.fromJson(edgeJsonArray, new TypeToken<List<DSSEdgeDefault>>() {
        }.getType());
        return edges;
    }

    @Override
    public List<String> getWorkFlowNodesJson(String workFlowJson) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(workFlowJson).getAsJsonObject();
        JsonArray nodeJsonArray = jsonObject.getAsJsonArray("nodes");
        if (nodeJsonArray == null) {
            return null;
        }
        List<Object> nodeJsonList = DSSCommonUtils.COMMON_GSON.fromJson(nodeJsonArray.toString(), new TypeToken<List<Object>>() {
        }.getType());
        return nodeJsonList.stream().map(DSSCommonUtils.COMMON_GSON::toJson).collect(Collectors.toList());
    }

    @Override
    public String updateFlowJsonWithKey(String workFlowJson, String key, Object value) throws IOException {
        if (value == null || key == null) {
            return workFlowJson;
        }
        Map<String, Object> flowJsonObject = BDPJettyServerHelper.jacksonJson().readValue(workFlowJson, Map.class);
        flowJsonObject.replace(key, value);
        String updatedJson = BDPJettyServerHelper.jacksonJson().writeValueAsString(flowJsonObject);
        return updatedJson;
    }

    @Override
    public String updateFlowJsonWithMap(String workFlowJson, Map<String, Object> props) throws JsonProcessingException {
        if (MapUtils.isEmpty(props)) {
            return workFlowJson;
        }
        Map<String, Object> flowJsonObject = BDPJettyServerHelper.jacksonJson().readValue(workFlowJson, Map.class);
        props.forEach(flowJsonObject::replace);
        return BDPJettyServerHelper.jacksonJson().writeValueAsString(flowJsonObject);
    }

    @Override
    public String getValueWithKey(String workFlowJson, String key) throws IOException {
        if (key == null) {
            return null;
        }
        Map<String, Object> flowJsonObject = BDPJettyServerHelper.jacksonJson().readValue(workFlowJson, Map.class);

        Object value = flowJsonObject.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }
}
