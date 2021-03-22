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

package com.webank.wedatasphere.dss.appconn.schedule.core.parser;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerEdge;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerEdgeDefault;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.node.DSSEdge;
import com.webank.wedatasphere.dss.common.entity.node.DSSEdgeDefault;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSJsonFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.Flow;
import org.springframework.beans.BeanUtils;

import java.util.*;


public abstract class AbstractFlowParser implements FlowParser {

    private NodeParser[] nodeParsers;

    @Override
    public void setNodeParsers(NodeParser[] nodeParsers) {
        this.nodeParsers = nodeParsers;
    }

    @Override
    public NodeParser[] getNodeParsers() {
        return nodeParsers;
    }

    //留白
    protected void downloadFlowResources() {
    }

    //留白
    protected void dealFlowResources() {
    }

    protected void dealFlowProperties(Flow flow) {
    }

    @Override
    public SchedulerFlow parseFlow(DSSJsonFlow flow) {
        downloadFlowResources();
        dealFlowResources();
        dealFlowProperties(flow);
        return resolveDWSJSONFlow(flow);
    }

    //    解析DWSJSONFlow，生成DWSNode
    public SchedulerFlow resolveDWSJSONFlow(DSSJsonFlow jsonFlow) {
        SchedulerFlow schedulerFlow = createSchedulerFlow();
        BeanUtils.copyProperties(jsonFlow, schedulerFlow, "children");
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonFlow.getJson()).getAsJsonObject();
        JsonArray nodeJsonArray = jsonObject.getAsJsonArray("nodes");
        Gson gson = new Gson();
        List<DSSNode> dwsNodes = gson.fromJson(nodeJsonArray, new TypeToken<List<DSSNodeDefault>>() {
        }.getType());
        List<SchedulerNode> schedulerNodeList = new ArrayList<>();
        List<SchedulerEdge> schedulerEdgeList = new ArrayList<>();
        if (null != dwsNodes) {
            for (DSSNode dwsNode : dwsNodes) {
                Optional<NodeParser> firstNodeParser = Arrays.stream(getNodeParsers())
                        .filter(p -> p.ifNodeCanParse(dwsNode))
                        .sorted((p1, p2) -> p2.getOrder() - p1.getOrder())
                        .findFirst();
                SchedulerNode schedulerNode = firstNodeParser.orElseThrow(() -> new IllegalArgumentException("NodeParser个数应该大于0")).parseNode(dwsNode);
                schedulerNodeList.add(schedulerNode);
            }
        }
        JsonArray edgeJsonArray = jsonObject.getAsJsonArray("edges");
        List<DSSEdge> dwsEdges = gson.fromJson(edgeJsonArray, new TypeToken<List<DSSEdgeDefault>>() {
        }.getType());
        if (dwsEdges != null){
            for (DSSEdge dwsEdge : dwsEdges) {
                SchedulerEdge schedulerEdge = new SchedulerEdgeDefault();
                schedulerEdge.setDSSEdge(dwsEdge);
                schedulerEdgeList.add(schedulerEdge);
            }
        }
        JsonArray proJsonArray = jsonObject.getAsJsonArray("props");
        List<Map<String, Object>> props = gson.fromJson(proJsonArray, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
        JsonArray resourcesJsonArray = jsonObject.getAsJsonArray("resources");
        List<Resource> resources = gson.fromJson(resourcesJsonArray, new TypeToken<List<Resource>>() {
        }.getType());

        JsonObject scheduleJsonObject = jsonObject.getAsJsonObject("scheduleParams");
        HashMap<String, Object> result = gson.fromJson(scheduleJsonObject, HashMap.class);
        schedulerFlow.setFlowResources(resources);
        schedulerFlow.setFlowProperties(props);
        schedulerFlow.setSchedulerEdges(schedulerEdgeList);
        schedulerFlow.setSchedulerNodes(schedulerNodeList);
        schedulerFlow.setFlowScheduleProperties(result);

        //update by peaceWong,add contextID to schedulerFlow
        if (jsonObject.has("contextID")) {
            String contextID = jsonObject.get("contextID").getAsString();
            schedulerFlow.setContextID(contextID);
        }
        schedulerFlow.setJsonFlow(jsonFlow);

        return schedulerFlow;
    }

    protected SchedulerFlow createSchedulerFlow() {
        return new SchedulerFlow();
    }


}
