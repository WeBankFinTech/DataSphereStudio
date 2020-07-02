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

package com.webank.wedatasphere.dss.appjoint.scheduler.tuning;

import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerEdge;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.constant.SchedulerAppJointConstant;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by enjoyyin on 2019/9/25.
 */
public abstract class AbstractFlowTuning implements FlowTuning {

    private NodeTuning[] nodeTunings;

    @Override
    public void setNodeTunings(NodeTuning[] nodeTunings) {
        this.nodeTunings = nodeTunings;
    }

    @Override
    public NodeTuning[] getNodeTunings() {
        return this.nodeTunings;
    }

    @Override
    public SchedulerFlow tuningSchedulerFlow(SchedulerFlow schedulerFlow) {
        //1.设置依赖，对于多个flowTuning，已经设置的就跳过
        schedulerFlow.getSchedulerNodes().forEach(node ->setDependencies(node,schedulerFlow.getSchedulerNodes(),schedulerFlow.getSchedulerEdges()));
        //2.设置usreproxy
        setProxyUser(schedulerFlow);
        // 2.调用各种nodeTuning进行
        List<SchedulerNode> schedulerNodes = schedulerFlow.getSchedulerNodes();
        for (NodeTuning nodeTuning : getNodeTunings()) {
            schedulerNodes = targetNodeTuning(nodeTuning, schedulerNodes);
        }
        schedulerFlow.setSchedulerNodes(schedulerNodes);
        //3.对子flow递归调用本方法
        List<SchedulerFlow> children = schedulerFlow.getChildren();
        if (children != null) {
            List<SchedulerFlow> collect = children.stream().map(this::tuningSchedulerFlow).collect(Collectors.toList());
            schedulerFlow.setChildren(collect);
        }
        return schedulerFlow;
    }

    private void setDependencies(SchedulerNode node,List<SchedulerNode> schedulerNodes,List<SchedulerEdge> flowEdges){
        //设置过的flow不在进行设置和解析
        if(node.getDependencys()!= null && !node.getDependencys().isEmpty()) return;
        List<String> dependencies = resolveDependencys(node,schedulerNodes ,flowEdges);
        dependencies.forEach(node::addDependency);
    }

    private List<String> resolveDependencys(SchedulerNode node,List<SchedulerNode> schedulerNodes, List<SchedulerEdge> flowEdges) {
        List<String> dependencys = new ArrayList<>();
        flowEdges.forEach(edge -> {
            if (edge.getDssEdge().getTarget().equals(node.getId())) {
                dependencys.add(schedulerNodes.stream().filter(n ->edge.getDssEdge().getSource().equals(n.getId())).findFirst().get().getName());
            }
        });

        return dependencys;
    }

    private List<SchedulerNode> targetNodeTuning(NodeTuning nodeTuning, List<SchedulerNode> schedulerNodes) {
        return schedulerNodes.stream().map(node -> invokeNodeTuning(nodeTuning, node)).collect(Collectors.toList());
    }

    private SchedulerNode invokeNodeTuning(NodeTuning nodeTuning, SchedulerNode schedulerNode) {
        if (nodeTuning.ifNodeCanTuning(schedulerNode)) {
            return nodeTuning.tuningSchedulerNode(schedulerNode);
        } else {
            return schedulerNode;
        }
    }

    private String getProxyUser(SchedulerFlow schedulerFlow) {
        if(schedulerFlow.getFlowProperties() == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        schedulerFlow.getFlowProperties().forEach( map -> {
            Object value = map.get(SchedulerAppJointConstant.PROXY_USER);
            if(value != null && StringUtils.isNotBlank(value.toString())) {
                sb.append(value.toString());
            }
        });
        return sb.toString();
    }

    private void setProxyUser(SchedulerFlow schedulerFlow) {
        String proxyUser = getProxyUser(schedulerFlow);
        if(StringUtils.isNotBlank(proxyUser)) {
            schedulerFlow.getSchedulerNodes().forEach(node -> node.getDssNode().setUserProxy(proxyUser));
            schedulerFlow.setUserProxy(proxyUser);
        }
    }

}
