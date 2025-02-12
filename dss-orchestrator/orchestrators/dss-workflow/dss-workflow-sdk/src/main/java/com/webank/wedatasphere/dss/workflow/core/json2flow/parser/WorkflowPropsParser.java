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

package com.webank.wedatasphere.dss.workflow.core.json2flow.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WorkflowPropsParser implements WorkflowParser {

    @Override
    public Workflow parse(JsonObject flowJson, Workflow workflow) {
        JsonArray proJsonArray = flowJson.getAsJsonArray("props");
        List<Map<String, Object>> props = DSSCommonUtils.COMMON_GSON.fromJson(proJsonArray, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
        JsonArray resourcesJsonArray = flowJson.getAsJsonArray("resources");
        List<Resource> resources = DSSCommonUtils.COMMON_GSON.fromJson(resourcesJsonArray, new TypeToken<List<Resource>>() {
        }.getType());
        if(workflow instanceof WorkflowImpl) {
            WorkflowImpl workflow1 = (WorkflowImpl) workflow;
            workflow1.setFlowResources(resources);
            workflow1.setFlowProperties(props);
        }
        return workflow;
    }

    @Override
    public int getOrder() {
        return 8;
    }
}
