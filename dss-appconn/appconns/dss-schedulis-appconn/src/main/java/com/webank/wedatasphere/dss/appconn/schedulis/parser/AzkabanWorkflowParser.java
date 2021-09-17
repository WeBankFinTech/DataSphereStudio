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

package com.webank.wedatasphere.dss.appconn.schedulis.parser;

import com.google.gson.JsonObject;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanWorkflow;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNodeEdge;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNodeImpl;
import com.webank.wedatasphere.dss.workflow.core.json2flow.parser.WorkflowParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;

public class AzkabanWorkflowParser implements WorkflowParser {

    @Override
    public Workflow parse(JsonObject flowJson, Workflow workflow) {
        AzkabanWorkflow azkabanWorkflow = new AzkabanWorkflow();
        try {
            BeanUtils.copyProperties(azkabanWorkflow, workflow);
        } catch (Exception e) {
            throw new DSSRuntimeException(91500, "Copy workflow fields failed!", e);
        }
        return addEndNodeForFlowName(azkabanWorkflow);
    }

    private AzkabanWorkflow addEndNodeForFlowName(AzkabanWorkflow flow) {
        DSSNodeDefault endNode = new DSSNodeDefault();
        List<WorkflowNode> endNodeList = getFlowEndJobList(flow);
        if(flow.getRootFlow()){
            endNode.setId(flow.getName());
            endNode.setName(flow.getName());
        }else{
            endNode.setId(flow.getName() + "_");
            endNode.setName(flow.getName() + "_");
        }
        endNode.setNodeType("linkis.control.empty");
        Map<String, Object> jobContentMap = new HashMap<>();
        endNode.setJobContent(jobContentMap);
        if (!endNodeList.isEmpty()) {
            if(endNodeList.size() == 1 ) {
                if(endNodeList.get(0).getName().equals(flow.getName())){
                    return flow;
                }
            }
            endNodeList.forEach(tmpNode -> endNode.addDependency(tmpNode.getName()));
            WorkflowNode azkabanSchedulerNode = new WorkflowNodeImpl();
            azkabanSchedulerNode.setDSSNode(endNode);
            flow.getWorkflowNodes().add(azkabanSchedulerNode);
        }
        return flow;
    }

    private List<WorkflowNode> getFlowEndJobList(AzkabanWorkflow flow) {
        List<WorkflowNode> res = new ArrayList<>();
        for (WorkflowNode job : flow.getWorkflowNodes()) {
            int flag = 0;
            for (WorkflowNodeEdge link : flow.getWorkflowNodeEdges()) {
                if (job.getId().equals(link.getDSSEdge().getSource())) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                res.add(job);
            }
        }
        return res;
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
