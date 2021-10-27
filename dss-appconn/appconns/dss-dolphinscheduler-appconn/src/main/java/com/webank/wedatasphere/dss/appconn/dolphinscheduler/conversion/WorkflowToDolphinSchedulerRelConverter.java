package com.webank.wedatasphere.dss.appconn.dolphinscheduler.conversion;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerConvertedRel;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTask;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerWorkflow;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinAppConnUtils;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.operation.WorkflowToRelConverter;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNodeEdge;

/**
 * The type Workflow to dolphin scheduler rel converter.
 *
 * @author yuxin.yuan
 * @date 2021/10/27
 */
public class WorkflowToDolphinSchedulerRelConverter implements WorkflowToRelConverter {

    public static final Logger logger = LoggerFactory.getLogger(WorkflowToDolphinSchedulerRelConverter.class);

    private static NodeConverter nodeConverter = new NodeConverter();

    @Override
    public ConvertedRel convertToRel(PreConversionRel rel) {
        DolphinSchedulerConvertedRel dolphinSchedulerConvertedRel = (DolphinSchedulerConvertedRel)rel;
        List<Workflow> workflows = dolphinSchedulerConvertedRel.getWorkflows();
        List<Workflow> dolphinSchedulerWorkflows =
            workflows.stream().map(workflow -> convertWorkflow(workflow)).collect(Collectors.toList());
        dolphinSchedulerConvertedRel.setWorkflows(dolphinSchedulerWorkflows);
        return dolphinSchedulerConvertedRel;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    private DolphinSchedulerWorkflow convertWorkflow(Workflow workflow) {
        DolphinSchedulerWorkflow dolphinSchedulerWorkflow = new DolphinSchedulerWorkflow();
        try {
            BeanUtils.copyProperties(dolphinSchedulerWorkflow, workflow);
        } catch (Exception e) {
            throw new DSSRuntimeException(91500, "Copy workflow fields failed!", e);
        }

        String contextID = dolphinSchedulerWorkflow.getContextID();
        String user = null;
        try {
            String value = DolphinAppConnUtils.getValueFromJsonString(contextID, "value");
            user = DolphinAppConnUtils.getValueFromJsonString(value, "user");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        DolphinSchedulerWorkflow.ProcessDefinitionJson processDefinitionJson =
            new DolphinSchedulerWorkflow.ProcessDefinitionJson();
        Map<String, DolphinSchedulerWorkflow.LocationInfo> locations = new HashMap<>();
        for (WorkflowNode workflowNode : workflow.getWorkflowNodes()) {
            DSSNode node = workflowNode.getDSSNode();

            DolphinSchedulerTask dolphinSchedulerTask = nodeConverter.conversion(node);
            processDefinitionJson.addTask(dolphinSchedulerTask);

            DolphinSchedulerWorkflow.LocationInfo locationInfo = new DolphinSchedulerWorkflow.LocationInfo();
            locationInfo.setName(node.getName());
            locationInfo.setTargetarr(StringUtils.join(node.getDependencys(), ","));
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
}
