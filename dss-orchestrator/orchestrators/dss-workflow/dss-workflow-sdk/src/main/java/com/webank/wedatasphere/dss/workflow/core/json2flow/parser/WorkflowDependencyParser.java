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

import com.google.gson.JsonObject;
import com.webank.wedatasphere.dss.workflow.core.constant.WorkflowConstant;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowImpl;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNodeEdge;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;


public class WorkflowDependencyParser implements WorkflowParser {

    @Override
    public Workflow parse(JsonObject flowJson, Workflow workflow) {
        if(!(workflow instanceof WorkflowImpl)) {
            return workflow;
        }
        //1.设置依赖，对于多个flowTuning，已经设置的就跳过
        WorkflowImpl realWorkflow = (WorkflowImpl) workflow;
        workflow.getWorkflowNodes()
            .forEach(node -> setDependencies(node, workflow.getWorkflowNodes(), workflow.getWorkflowNodeEdges()));
        //2.设置usreproxy
        setProxyUser(realWorkflow);
        return workflow;
    }

    private void setDependencies(WorkflowNode node, List<WorkflowNode> workflowNodes, List<WorkflowNodeEdge> flowEdges){
        //设置过的flow不在进行设置和解析
        if(node.getDependencys()!= null && !node.getDependencys().isEmpty()) {
            return;
        }
        List<String> dependencies = resolveDependencies(node, workflowNodes ,flowEdges);
        dependencies.forEach(node::addDependency);
    }

    private List<String> resolveDependencies(WorkflowNode node, List<WorkflowNode> workflowNodes, List<WorkflowNodeEdge> flowEdges) {
        List<String> dependencies = new ArrayList<>();
        flowEdges.forEach(edge -> {
            if (edge.getDSSEdge().getTarget().equals(node.getId())) {
                dependencies.add(workflowNodes.stream().filter(n ->edge.getDSSEdge().getSource().equals(n.getId())).findFirst().get().getName());
            }
        });

        return dependencies;
    }

    private String getProxyUser(Workflow workflow) {
        if(workflow.getFlowProperties() == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        workflow.getFlowProperties().forEach( map -> {
            Object value = map.get(WorkflowConstant.PROXY_USER);
            if(value != null && StringUtils.isNotBlank(value.toString())) {
                sb.append(value.toString());
            }
        });
        return sb.toString();
    }

    private void setProxyUser(WorkflowImpl workflow) {
        String proxyUser = getProxyUser(workflow);
        if(StringUtils.isNotBlank(proxyUser)) {
            workflow.getWorkflowNodes().forEach(node -> node.getDSSNode().setUserProxy(proxyUser));
            workflow.setUserProxy(proxyUser);
        }
    }

    @Override
    public int getOrder() {
        return 20;
    }

}
