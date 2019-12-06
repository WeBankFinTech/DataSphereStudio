/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appjoint.scheduler.parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerEdge;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerEdgeDefault;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DWSJSONFlow;
import com.webank.wedatasphere.dss.common.entity.flow.Flow;
import com.webank.wedatasphere.dss.common.entity.node.DWSEdge;
import com.webank.wedatasphere.dss.common.entity.node.DWSEdgeDefault;
import com.webank.wedatasphere.dss.common.entity.node.DWSNode;
import com.webank.wedatasphere.dss.common.entity.node.DWSNodeDefault;
import org.springframework.beans.BeanUtils;

import java.util.*;


/**
 * Created by enjoyyin on 2019/9/7.
 */
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
    protected void  downloadFlowResources(){}

    //留白
    protected void dealFlowResources(){}

    protected void dealFlowProperties(Flow flow){}

    @Override
    public SchedulerFlow parseFlow(DWSJSONFlow flow) {
        downloadFlowResources();
        dealFlowResources();
        dealFlowProperties(flow);
        return resolveDWSJSONFlow(flow);
    }

    //    解析DWSJSONFlow，生成DWSNode
    public SchedulerFlow resolveDWSJSONFlow(DWSJSONFlow jsonFlow){
        SchedulerFlow schedulerFlow = createSchedulerFlow();
        BeanUtils.copyProperties(jsonFlow,schedulerFlow,"children");
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonFlow.getJson()).getAsJsonObject();
        JsonArray nodeJsonArray = jsonObject.getAsJsonArray("nodes");
        Gson gson = new Gson();
        List<DWSNode> dwsNodes = gson.fromJson(nodeJsonArray, new TypeToken<List<DWSNodeDefault>>() {
        }.getType());
        List<SchedulerNode> schedulerNodeList = new ArrayList<>();
        List<SchedulerEdge> schedulerEdgeList = new ArrayList<>();
        for (DWSNode dwsNode : dwsNodes) {
            Optional<NodeParser> firstNodeParser = Arrays.stream(getNodeParsers())
                    .filter(p -> p.ifNodeCanParse(dwsNode))
                    .sorted((p1, p2) -> p2.getOrder() - p1.getOrder())
                    .findFirst();
            SchedulerNode schedulerNode = firstNodeParser.orElseThrow(()->new IllegalArgumentException("NodeParser个数应该大于0")).parseNode(dwsNode);
            schedulerNodeList.add(schedulerNode);
        }
        JsonArray edgeJsonArray = jsonObject.getAsJsonArray("edges");
        List<DWSEdge> dwsEdges = gson.fromJson(edgeJsonArray, new TypeToken<List<DWSEdgeDefault>>() {
        }.getType());
        for (DWSEdge dwsEdge : dwsEdges) {
            SchedulerEdge schedulerEdge = new SchedulerEdgeDefault();
            schedulerEdge.setDWSEdge(dwsEdge);
            schedulerEdgeList.add(schedulerEdge);

        }
        JsonArray proJsonArray = jsonObject.getAsJsonArray("props");
        List<Map<String, Object>> props = gson.fromJson(proJsonArray, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
        JsonArray resourcesJsonArray = jsonObject.getAsJsonArray("resources");
        List<Resource> resources = gson.fromJson(resourcesJsonArray, new TypeToken<List<Resource>>() {
        }.getType());
        schedulerFlow.setFlowResources(resources);
        schedulerFlow.setFlowProperties(props);
        schedulerFlow.setSchedulerEdges(schedulerEdgeList);
        schedulerFlow.setSchedulerNodes(schedulerNodeList);
        return schedulerFlow;
    }

    protected SchedulerFlow createSchedulerFlow(){return new SchedulerFlow();}
}
