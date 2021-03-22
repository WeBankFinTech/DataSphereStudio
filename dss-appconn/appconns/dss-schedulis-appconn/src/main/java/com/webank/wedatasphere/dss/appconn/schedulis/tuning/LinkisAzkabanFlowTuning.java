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

package com.webank.wedatasphere.dss.appconn.schedulis.tuning;


import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanSchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanSchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.LinkisAzkabanSchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerEdge;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.AbstractFlowTuning;
import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.NodeTuning;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//DefaultFlowTuning修改为AzkabanFlowTuning
public class LinkisAzkabanFlowTuning extends AbstractFlowTuning {



    @Override
    public SchedulerFlow tuningSchedulerFlow(SchedulerFlow schedulerFlow) {
        //1.尾部添加依赖
        //2.设置子flow和node存储路径
        SchedulerFlow addEndNodeSchedulerFlow = addEndNodeForFlowName(schedulerFlow);
        String flowStorePath = ((AzkabanSchedulerFlow) addEndNodeSchedulerFlow).getStorePath();
        if (addEndNodeSchedulerFlow.getChildren() != null) {
            addEndNodeSchedulerFlow.getChildren().forEach(flow -> setFlowStorePath(flowStorePath, flow));
        }

        if (addEndNodeSchedulerFlow.getSchedulerNodes() != null) {
            addEndNodeSchedulerFlow.getSchedulerNodes().forEach(node -> setNodeStorePath(flowStorePath, node));
        }
        return super.tuningSchedulerFlow(schedulerFlow);
    }

    private void setFlowStorePath(String flowStorePath, SchedulerFlow schedulerFlow) {
        AzkabanSchedulerFlow azkabanSchedulerFlow = (AzkabanSchedulerFlow) schedulerFlow;
        azkabanSchedulerFlow.setStorePath(flowStorePath + File.separator + "subFlows" + File.separator + azkabanSchedulerFlow.getName());
    }

    private void setNodeStorePath(String flowStorePath, SchedulerNode schedulerNode) {
        AzkabanSchedulerNode azkabanSchedulerNode = (AzkabanSchedulerNode) schedulerNode;
        azkabanSchedulerNode.setStorePath(flowStorePath + File.separator + "jobs" + File.separator + schedulerNode.getName());
    }

    @Override
    public NodeTuning[] getNodeTunings() {
        return new NodeTuning[0];
    }

    @Override
    public Boolean ifFlowCanTuning(SchedulerFlow schedulerFlow) {
        // TODO: 2019/9/30 应该加上node 是linkisazkabanNode的 instance判断，因为加到尾部节点的就是一个linkisazkabanNode
        return schedulerFlow instanceof AzkabanSchedulerFlow;
    }

    private SchedulerFlow addEndNodeForFlowName(SchedulerFlow flow) {
        DSSNodeDefault endNode = new DSSNodeDefault();
        List<SchedulerNode> endNodeList = getFlowEndJobList(flow);
        if(flow.getRootFlow()){
            endNode.setId(flow.getName());
            endNode.setName(flow.getName());
        }else{
            endNode.setId(flow.getName() + "_");
            endNode.setName(flow.getName() + "_");
        }
        endNode.setNodeType("linkis.control.empty");
        Map jobContentMap = new java.util.HashMap();
        /*jobContentMap.put("script", "");*/
        endNode.setJobContent(jobContentMap);
        if (endNodeList.size() > 0) {
            if(endNodeList.size() == 1 )
            {
                if(endNodeList.get(0).getName().equals(flow.getName())){
                    //不需要添加
                    return flow;
                }
            }
            endNodeList.forEach(tmpNode -> endNode.addDependency(tmpNode.getName()));
            LinkisAzkabanSchedulerNode azkabanSchedulerNode = new LinkisAzkabanSchedulerNode();
            azkabanSchedulerNode.setDSSNode(endNode);
            flow.getSchedulerNodes().add((azkabanSchedulerNode));
         }
         return flow;
    }

    private List<SchedulerNode> getFlowEndJobList(SchedulerFlow flow) {
        List<SchedulerNode> res = new ArrayList<>();
        for (SchedulerNode job : flow.getSchedulerNodes()) {
            int flag = 0;
            for (SchedulerEdge link : flow.getSchedulerEdges()) {
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
        return 1;
    }
}
