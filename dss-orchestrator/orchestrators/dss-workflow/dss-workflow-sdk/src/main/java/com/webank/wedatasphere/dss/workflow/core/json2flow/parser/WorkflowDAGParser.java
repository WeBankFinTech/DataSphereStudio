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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.common.entity.node.DSSEdge;
import com.webank.wedatasphere.dss.common.entity.node.DSSEdgeDefault;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.common.utils.ClassUtils;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowImpl;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNodeEdge;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNodeEdgeImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class WorkflowDAGParser implements WorkflowParser {

    private List<WorkflowNodeParser> workflowNodeParsers;

    public synchronized void addWorkflowNodeParser(WorkflowNodeParser workflowNodeParser) {
        if(workflowNodeParsers == null) {
            workflowNodeParsers = new ArrayList<>();
        }
        this.workflowNodeParsers.add(workflowNodeParser);
    }

    protected List<WorkflowNodeParser> getWorkflowNodeParsers() {
        return workflowNodeParsers;
    }

    @Override
    public void init() {
        if(workflowNodeParsers == null) {
            workflowNodeParsers = ClassUtils.getInstances(WorkflowNodeParser.class);
        } else {
            workflowNodeParsers.addAll(ClassUtils.getInstances(WorkflowNodeParser.class));
        }
    }

    @Override
    public Workflow parse(JsonObject flowJson, Workflow workflow) {
        JsonArray nodeJsonArray = flowJson.getAsJsonArray("nodes");
        Gson gson = DSSCommonUtils.COMMON_GSON;
        List<DSSNode> dwsNodes = gson.fromJson(nodeJsonArray, new TypeToken<List<DSSNodeDefault>>() {
        }.getType());
        List<WorkflowNode> workflowNodeList = new ArrayList<>();
        List<WorkflowNodeEdge> workflowNodeEdgeList = new ArrayList<>();
        if (null != dwsNodes) {
            for (DSSNode dwsNode : dwsNodes) {
                Optional<WorkflowNodeParser> firstNodeParser = workflowNodeParsers.stream()
                    .filter(p -> p.ifNodeCanParse(dwsNode))
                    .min((p1, p2) -> p2.getOrder() - p1.getOrder());
                WorkflowNode workflowNode = firstNodeParser.orElseThrow(() -> new IllegalArgumentException("NodeParser个数应该大于0")).parseNode(dwsNode);
                workflowNodeList.add(workflowNode);
            }
        }
        JsonArray edgeJsonArray = flowJson.getAsJsonArray("edges");
        List<DSSEdge> dwsEdges = gson.fromJson(edgeJsonArray, new TypeToken<List<DSSEdgeDefault>>() {
        }.getType());
        if (dwsEdges != null){
            for (DSSEdge dwsEdge : dwsEdges) {
                WorkflowNodeEdge workflowNodeEdge = new WorkflowNodeEdgeImpl();
                workflowNodeEdge.setDSSEdge(dwsEdge);
                workflowNodeEdgeList.add(workflowNodeEdge);
            }
        }
        if(workflow instanceof WorkflowImpl) {
            WorkflowImpl workflow1 = (WorkflowImpl) workflow;
            workflow1.setWorkflowNodeEdges(workflowNodeEdgeList);
            workflow1.setWorkflowNodes(workflowNodeList);
        }
        return workflow;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
