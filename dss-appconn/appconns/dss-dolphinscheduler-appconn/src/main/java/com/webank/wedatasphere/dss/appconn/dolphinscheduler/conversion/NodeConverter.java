package com.webank.wedatasphere.dss.appconn.dolphinscheduler.conversion;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerConvertedRel;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTask;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTaskParam;
import com.webank.wedatasphere.dss.appconn.scheduler.utils.SchedulerConf;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.OrchestrationToRelConversionRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationWarnException;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowWithContextImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.linkis.common.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf.DSS_DOLPHINSCHEDULER_CLIENT_HOME;

public class NodeConverter {

    private static final Logger logger = LoggerFactory.getLogger(NodeConverter.class);

    public NodeConverter() {
    }

    /**
     * 将DSS中节点转为 DolphinScheduler 中节点 task.
     *
     * @param dssNode
     *            the scheduler node
     * @return the dolphin scheduler task
     */
    public DolphinSchedulerTask convertNode(DolphinSchedulerConvertedRel dolphinSchedulerConvertedRel,
                                            DSSNode dssNode) {
        OrchestrationToRelConversionRequestRef ref = dolphinSchedulerConvertedRel.getDSSToRelConversionRequestRef();
        DolphinSchedulerTask task = new DolphinSchedulerTask();
        task.setId(dssNode.getId());
        task.setName(dssNode.getName());
        task.setPreTasks(dssNode.getDependencys());
        task.setType("SHELL");
        String nodeStr = String.join("-", ref.getWorkspace().getWorkspaceName(),
                ref.getDSSProject().getName(), ref.getDSSOrchestration().getName(), dssNode.getName());
        Map<String, Object> sourceMap = MapUtils.newCommonMapBuilder().put("workspaceName", ref.getWorkspace().getWorkspaceName())
                .put("projectName", ref.getDSSProject().getName())
                .put("flowName", ref.getDSSOrchestration().getName()).put("nodeName", dssNode.getName()).build();
        WorkflowWithContextImpl workflow = (WorkflowWithContextImpl) dolphinSchedulerConvertedRel.getWorkflow();
        DolphinSchedulerTaskParam taskParams = new DolphinSchedulerTaskParam();
        try {
            List<String> scriptList = new ArrayList<>();
            BiConsumer<String, String> addLine = (key, value) -> scriptList.add(String.format("export %s='%s'", key, value));
            BiConsumer<String, Object> addObjectLine = (key, value) -> {
                if(value == null) {
                    return;
                }
                String valueStr = DSSCommonUtils.COMMON_GSON.toJson(value);
                valueStr = valueStr.replaceAll("\"", "\\\"");
                addLine.accept(key, valueStr);
            };
            addLine.accept("LINKIS_TYPE", dssNode.getNodeType());
            addLine.accept("PROXY_USER", dssNode.getUserProxy());
            addLine.accept("LINKIS_VERSION", DolphinSchedulerConf.LINKIS_1_X_VERSION.getValue());
            addObjectLine.accept("JOB_COMMAND", dssNode.getJobContent());
            addObjectLine.accept("JOB_PARAMS", dssNode.getParams());
            addObjectLine.accept("JOB_RESOURCES", dssNode.getResources());
            addObjectLine.accept("JOB_SOURCE", sourceMap);
            addLine.accept("CONTEXT_ID", workflow.getContextID());
            addLine.accept("LINKIS_GATEWAY_URL", Configuration.getGateWayURL());
            addLine.accept("DS_VERSION", DolphinSchedulerConf.DS_VERSION.getValue());
            //todo
            addLine.accept("RUN_DATE", "${global_run_date}");
            addObjectLine.accept("JOB_LABELS", new EnvDSSLabel(SchedulerConf.JOB_LABEL.getValue()).getValue());
            if(CollectionUtils.isNotEmpty(workflow.getFlowResources())) {
                addObjectLine.accept("FLOW_RESOURCES", workflow.getFlowResources());
            }
            if(CollectionUtils.isNotEmpty(workflow.getFlowProperties())) {
                addObjectLine.accept("FLOW_PROPERTIES", workflow.getFlowProperties());
            }
            String executionScript = String.join(" ", "sh",
                    DSS_DOLPHINSCHEDULER_CLIENT_HOME.getValue() + "/bin/dss-dolphinscheduler-client.sh",
                    nodeStr);
            scriptList.add(executionScript);
            taskParams.setRawScript(String.join("\n", scriptList));
        } catch (Exception e) {
            logger.error("工作流节点 {} 转换失败.", dssNode.getName(), e);
            throw new ExternalOperationWarnException(90321, "工作流节点 " + dssNode.getName() + " 转换成DolphinScheduler节点失败！", e);
        }
        task.setParams(taskParams);
        return task;
    }

}
