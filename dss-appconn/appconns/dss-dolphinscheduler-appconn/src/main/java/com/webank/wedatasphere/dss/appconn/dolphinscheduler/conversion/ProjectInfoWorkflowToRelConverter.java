package com.webank.wedatasphere.dss.appconn.dolphinscheduler.conversion;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerConvertedRel;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerUtilsScala;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.entity.PreConversionRel;
import com.webank.wedatasphere.dss.workflow.conversion.operation.WorkflowToRelConverter;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;

/**
 * The type Project info workflow to rel converter.
 *
 * @author yuxin.yuan
 * @date 2021/10/27
 */
public class ProjectInfoWorkflowToRelConverter implements WorkflowToRelConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectInfoWorkflowToRelConverter.class);

    @Override
    public ConvertedRel convertToRel(PreConversionRel rel) {
        List<String> repeatNode = DolphinSchedulerUtilsScala.getRepeatNodeName(getAllNodeName(rel.getWorkflows()));
        if (!repeatNode.isEmpty()) {
            throw new DSSRuntimeException(80001, "重复的节点名称：" + repeatNode.toString());
        }
        DolphinSchedulerConvertedRel dolphinSchedulerConvertedRel = new DolphinSchedulerConvertedRel(rel);

        return dolphinSchedulerConvertedRel;
    }

    /**
     * Get the names of all nodes directly from allflows without recursion.
     */
    private List<String> getAllNodeName(List<Workflow> workflows) {
        List<String> nodeNames = new ArrayList<>();
        workflows.forEach(flow -> flow.getWorkflowNodes().forEach(node -> nodeNames.add(node.getName())));
        return nodeNames;
    }

    @Override
    public int getOrder() {
        return 5;
    }
}
