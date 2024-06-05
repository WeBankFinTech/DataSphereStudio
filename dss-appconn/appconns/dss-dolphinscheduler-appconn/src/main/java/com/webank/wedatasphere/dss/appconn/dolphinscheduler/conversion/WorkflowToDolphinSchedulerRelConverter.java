package com.webank.wedatasphere.dss.appconn.dolphinscheduler.conversion;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerConvertedRel;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTask;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerWorkflow;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.operation.WorkflowToRelConverter;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNodeEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkflowToDolphinSchedulerRelConverter implements WorkflowToRelConverter {

    public static final Logger logger = LoggerFactory.getLogger(WorkflowToDolphinSchedulerRelConverter.class);

    private static NodeConverter nodeConverter = new NodeConverter();

    @Override
    public ConvertedRel convertToRel(PreConversionRel rel) {
        DolphinSchedulerConvertedRel dolphinSchedulerConvertedRel = (DolphinSchedulerConvertedRel)rel;
        List<WorkflowNode> workflowNodes = dolphinSchedulerConvertedRel.getWorkflow().getWorkflowNodes();
        for (WorkflowNode workflowNode : workflowNodes) {
            if ("workflow.subflow".equals(workflowNode.getNodeType())) {
                throw new ExternalOperationFailedException(90021, workflowNode.getName() + "当前不支持将subFlow节点发布到DolphinScheduler！");
            }
        }
        Workflow dolphinSchedulerWorkflow = convertWorkflow(dolphinSchedulerConvertedRel);
        dolphinSchedulerConvertedRel.setWorkflow(dolphinSchedulerWorkflow);
        return dolphinSchedulerConvertedRel;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    private DolphinSchedulerWorkflow convertWorkflow(DolphinSchedulerConvertedRel dolphinSchedulerConvertedRel) {
        DolphinSchedulerWorkflow dolphinSchedulerWorkflow = new DolphinSchedulerWorkflow();
        Workflow workflow = dolphinSchedulerConvertedRel.getWorkflow();
        try {
            BeanUtils.copyProperties(workflow, dolphinSchedulerWorkflow);
        } catch (Exception e) {
            throw new DSSRuntimeException(91500, "Copy workflow fields failed!", e);
        }
        DolphinSchedulerWorkflow.ProcessDefinitionJson processDefinitionJson =
            new DolphinSchedulerWorkflow.ProcessDefinitionJson();
        processDefinitionJson.setGlobalParams(convertGlobalParams(workflow.getFlowProperties()));
        processDefinitionJson.getGlobalParams().add(new HashMap<String, Object>() {{
            put("prop", "global_run_date");
            put("value", "${system.biz.date}");
            put("type", "VARCHAR");
            put("direct", "IN");
        }});
        Map<String, DolphinSchedulerWorkflow.LocationInfo> locations = new HashMap<>();
        Map<String, String> nameToIdMap = workflow.getWorkflowNodes().stream()
                .collect(Collectors.toMap(WorkflowNode::getName, WorkflowNode::getId));
        for (WorkflowNode workflowNode : workflow.getWorkflowNodes()) {
            DSSNode node = workflowNode.getDSSNode();
            if (node.getLayout() == null) {
                continue;
            }
            DolphinSchedulerTask dolphinSchedulerTask = nodeConverter.convertNode(dolphinSchedulerConvertedRel, node);
            processDefinitionJson.addTask(dolphinSchedulerTask);

            DolphinSchedulerWorkflow.LocationInfo locationInfo = new DolphinSchedulerWorkflow.LocationInfo();
            locationInfo.setName(node.getName());
            String targetArr = node.getDependencys().stream().map(nameToIdMap::get).collect(Collectors.joining(","));
            locationInfo.setTargetarr(targetArr);
            locationInfo.setX((int)node.getLayout().getX());
            locationInfo.setY((int)node.getLayout().getY());
            locations.put(node.getId(), locationInfo);
        }

        List<WorkflowNodeEdge> workflowNodeEdges = workflow.getWorkflowNodeEdges();
        List<DolphinSchedulerWorkflow.Connect> connects = new LinkedList<>();
        for (WorkflowNodeEdge edge : workflowNodeEdges) {
            connects.add(
                new DolphinSchedulerWorkflow.Connect(edge.getDSSEdge().getSource(), edge.getDSSEdge().getTarget()));
        }

        dolphinSchedulerWorkflow.setProcessDefinitionJson(processDefinitionJson);
        dolphinSchedulerWorkflow.setLocations(locations);
        dolphinSchedulerWorkflow.setConnects(connects);

        return dolphinSchedulerWorkflow;
    }

    private List<Map<String, Object>> convertGlobalParams(List<Map<String, Object>> globalParams) {
        return globalParams.stream().map(map -> {
            Map<String, Object> ret = new HashMap<>();
            map.forEach((k, v) -> {
                ret.put("prop", k);
                ret.put("value", v);
                ret.put("type", "VARCHAR");
                ret.put("direct", "IN");
            });
            return ret;
        }).collect(Collectors.toList());
    }
}
