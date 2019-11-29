package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.tuning;


import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.AzkabanSchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.AzkabanSchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.entity.LinkisAzkabanSchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerEdge;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.AbstractFlowTuning;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.NodeTuning;
import com.webank.wedatasphere.dss.common.entity.node.DWSNodeDefault;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//DefaultFlowTuning修改为AzkabanFlowTuning
@Component
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
        DWSNodeDefault endNode = new DWSNodeDefault();
        List<SchedulerNode> endNodeList = getFlowEndJobList(flow);
        endNode.setId(flow.getName() + "_");
        endNode.setName(flow.getName() + "_");
        endNode.setNodeType("linkis.control.empty");
        Map jobContentMap = new java.util.HashMap();
        /*jobContentMap.put("script", "");*/
        endNode.setJobContent(jobContentMap);
        if (endNodeList.size() > 0) {
            endNodeList.forEach(tmpNode -> endNode.addDependency(tmpNode.getName()));
        }
        LinkisAzkabanSchedulerNode azkabanSchedulerNode = new LinkisAzkabanSchedulerNode();
        azkabanSchedulerNode.setDWSNode(endNode);
        flow.getSchedulerNodes().add((azkabanSchedulerNode));
        return flow;
    }

    private List<SchedulerNode> getFlowEndJobList(SchedulerFlow flow) {
        List<SchedulerNode> res = new ArrayList<>();
        for (SchedulerNode job : flow.getSchedulerNodes()) {
            int flag = 0;
            for (SchedulerEdge link : flow.getSchedulerEdges()) {
                if (job.getId().equals(link.getDWSEdge().getSource())) {
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
