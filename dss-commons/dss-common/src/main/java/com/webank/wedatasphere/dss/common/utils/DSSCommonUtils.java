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

package com.webank.wedatasphere.dss.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.utils.JsonUtils;

import java.util.List;
import java.util.stream.Collectors;

public class DSSCommonUtils {

    public static final String FLOW_RESOURCE_NAME = "resources";

    public static final String FLOW_EDGES_NAME = "edges";

    public static final String FLOW_PARENT_NAME = "parent";

    public static final String NODE_RESOURCE_NAME = "resources";

    public static final String FLOW_NODE_NAME = "nodes";

    public static final String FLOW_PROP_NAME = "props";

    public static final String NODE_PROP_NAME = "params";


    public static final String NODE_ID_NAME = "id";

    public static final String NODE_NAME_NAME = "title";

    public static final Gson COMMON_GSON = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls()
            .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (t, type, jsonSerializationContext) -> {
                if (t == t.longValue()) {
                    return new JsonPrimitive(t.longValue());
                } else {
                    return new JsonPrimitive(t);
                }
            }).create();

    public static final ObjectMapper JACKSON = JsonUtils.jackson();

    public static final String ENV_LABEL_VALUE_DEV = "dev";

    public static final String DSS_LABELS_KEY = "labels";
    public static final String DSS_EXECUTE_BY_PROXY_USER_KEY = "execByProxyUser";

    public static final CommonVars<String> DSS_HOME = CommonVars.apply("DSS_HOME", "");

    public static long parseToLong(Object val) {
        if (val instanceof Double) {
            return ((Double) val).longValue();
        } else if (val instanceof Integer) {
            return new Double((Integer) val).longValue();
        } else if (val instanceof Long) {
            return (Long) val;
        } else if (val != null) {
            return Long.parseLong(val.toString());
        }
        throw new DSSRuntimeException(90322, "parse the return of externalSystem failed, the value is null.");
    }

    public static List<DSSNodeDefault> getWorkFlowNodes(String workFlowJson) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(workFlowJson).getAsJsonObject();
        JsonArray nodeJsonArray = jsonObject.getAsJsonArray("nodes");
        List<DSSNodeDefault> dwsNodes = DSSCommonUtils.COMMON_GSON.fromJson(nodeJsonArray, new TypeToken<List<DSSNodeDefault>>() {
        }.getType());
        return dwsNodes;
    }

    public static List<String> getWorkFlowNodesJson(String workFlowJson) {
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

}
