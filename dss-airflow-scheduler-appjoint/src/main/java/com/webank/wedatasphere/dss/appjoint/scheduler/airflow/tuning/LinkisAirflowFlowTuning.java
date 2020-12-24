package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.tuning;


import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.constant.AirflowConstant;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.AirflowSchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.AirflowSchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.LinkisAirflowSchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.AbstractFlowTuning;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.NodeTuning;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class LinkisAirflowFlowTuning extends AbstractFlowTuning {



    @Override
    public SchedulerFlow tuningSchedulerFlow(SchedulerFlow schedulerFlow) {
        //1.首部和尾部添加依赖
        //2.设置子flow和node存储路径
        SchedulerFlow addEndNodeSchedulerFlow = addStartNodeForFlowName(addEndNodeForFlowName(schedulerFlow));
        String flowStorePath = ((AirflowSchedulerFlow) addEndNodeSchedulerFlow).getStorePath();
        if (addEndNodeSchedulerFlow.getChildren() != null) {
            addEndNodeSchedulerFlow.getChildren().forEach(flow -> setFlowStorePath(flowStorePath, flow));
        }

        if (addEndNodeSchedulerFlow.getSchedulerNodes() != null) {
            addEndNodeSchedulerFlow.getSchedulerNodes().forEach(node -> setNodeStorePath(flowStorePath, node));
        }
        return super.tuningSchedulerFlow(schedulerFlow);
    }

    private void setFlowStorePath(String flowStorePath, SchedulerFlow schedulerFlow) {
        AirflowSchedulerFlow airflowSchedulerFlow = (AirflowSchedulerFlow) schedulerFlow;
        airflowSchedulerFlow.setStorePath(flowStorePath + File.separator + "subFlows" + File.separator + airflowSchedulerFlow.getName());
    }

    private void setNodeStorePath(String flowStorePath, SchedulerNode schedulerNode) {
        AirflowSchedulerNode airflowSchedulerNode = (AirflowSchedulerNode) schedulerNode;
        airflowSchedulerNode.setStorePath(flowStorePath + File.separator + "jobs" + File.separator + schedulerNode.getName());
    }

    @Override
    public NodeTuning[] getNodeTunings() {
        return new NodeTuning[0];
    }

    @Override
    public Boolean ifFlowCanTuning(SchedulerFlow schedulerFlow) {
        // TODO: 2019/9/30 应该加上node 是linkisairflowNode的 instance判断，因为加到尾部节点的就是一个linkisairflowNode
        return schedulerFlow instanceof AirflowSchedulerFlow;
    }

    private SchedulerFlow addStartNodeForFlowName(SchedulerFlow flow) {
        DSSNodeDefault newStartNode = new DSSNodeDefault();
        List<SchedulerNode> oriStartNodeList = getFlowStartJobList(flow);
        newStartNode.setId(flow.getName() + AirflowConstant.FLOW_DUMMY_START_NODE_SUFFIX);
        newStartNode.setName(flow.getName() + AirflowConstant.FLOW_DUMMY_START_NODE_SUFFIX);
        newStartNode.setNodeType("linkis.control.empty");
        Map<String, Object> jobContentMap = new java.util.HashMap<>();
        newStartNode.setJobContent(jobContentMap);
        if (oriStartNodeList.size() > 0) {
            oriStartNodeList.forEach(tmpNode -> {
                tmpNode.addDependency(newStartNode.getName());
            });
        }
        LinkisAirflowSchedulerNode airflowSchedulerNode = new LinkisAirflowSchedulerNode();
        airflowSchedulerNode.setDssNode(newStartNode);
        flow.getSchedulerNodes().add((airflowSchedulerNode));
        return flow;
    }

    private List<SchedulerNode> getFlowStartJobList(SchedulerFlow flow) {
        List<SchedulerNode> res = new ArrayList<>();
        for (SchedulerNode job : flow.getSchedulerNodes()) {
            if (job.getDependencys() == null || job.getDependencys().isEmpty()) {
                res.add(job);
            }
        }
        return res;
    }

    private SchedulerFlow addEndNodeForFlowName(SchedulerFlow flow) {
        DSSNodeDefault endNode = new DSSNodeDefault();
        List<SchedulerNode> endNodeList = getFlowEndJobList(flow);
        endNode.setId(flow.getName() + AirflowConstant.FLOW_DUMMY_END_NODE_SUFFIX);
        endNode.setName(flow.getName() + AirflowConstant.FLOW_DUMMY_END_NODE_SUFFIX);
        endNode.setNodeType("linkis.control.empty");
        Map jobContentMap = new java.util.HashMap();
        /*jobContentMap.put("script", "");*/
        endNode.setJobContent(jobContentMap);
        if (endNodeList.size() > 0) {
            endNodeList.forEach(tmpNode -> endNode.addDependency(tmpNode.getName()));
        }
        LinkisAirflowSchedulerNode airflowSchedulerNode = new LinkisAirflowSchedulerNode();
        airflowSchedulerNode.setDssNode(endNode);
        flow.getSchedulerNodes().add((airflowSchedulerNode));
        return flow;
    }

    private List<SchedulerNode> getFlowEndJobList(SchedulerFlow flow) {
        List<SchedulerNode> res = new ArrayList<>();
        Set<String> nodeNameSet = new HashSet<>();
        flow.getSchedulerNodes().forEach(node -> nodeNameSet.add(node.getName()));
        for (SchedulerNode job : flow.getSchedulerNodes()) {
            for (String dependNodeName: job.getDependencys()) {
                nodeNameSet.remove(dependNodeName);
            }
        }
        return flow.getSchedulerNodes().stream()
                .filter(x -> nodeNameSet.contains(x.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
