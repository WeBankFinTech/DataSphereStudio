package com.webank.wedatasphere.dss.appconn.dolphinscheduler.conversion;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerConvertedRel;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTask;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTaskParam;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.OrchestrationToRelConversionRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationWarnException;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowWithContextImpl;
import org.apache.commons.collections4.CollectionUtils;
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
            BiConsumer<String, String> addLine = (key, value) -> scriptList.add(String.format("export %s=\"%s\"", key, value));
            addLine.accept("LINKIS_TYPE", dssNode.getNodeType());
            addLine.accept("PROXY_USER", dssNode.getUserProxy());
            addLine.accept("JOB_COMMAND", DSSCommonUtils.COMMON_GSON.toJson(dssNode.getJobContent())+"\"");
            addLine.accept("JOB_PARAMS", DSSCommonUtils.COMMON_GSON.toJson(dssNode.getParams()));
            addLine.accept("JOB_RESOURCES", DSSCommonUtils.COMMON_GSON.toJson(dssNode.getResources()));
            addLine.accept("JOB_SOURCE", DSSCommonUtils.COMMON_GSON.toJson(sourceMap));
            addLine.accept("CONTEXT_ID", workflow.getContextID());
            addLine.accept("LINKIS_GATEWAY_URL", ref.getWorkspace().getDssUrl());
            addLine.accept("RUN_DATE", "${system.biz.date}");
            if(CollectionUtils.isNotEmpty(workflow.getFlowResources())) {
                addLine.accept("FLOW_RESOURCES", DSSCommonUtils.COMMON_GSON.toJson(workflow.getFlowResources()));
            }
            if(CollectionUtils.isNotEmpty(workflow.getFlowProperties())) {
                addLine.accept("FLOW_PROPERTIES", DSSCommonUtils.COMMON_GSON.toJson(workflow.getFlowProperties()));
            }
            String executionScript = String.join(" ", "sh",
                    DSS_DOLPHINSCHEDULER_CLIENT_HOME.getValue() + "/dss-dolphinscheduler-client.sh",
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