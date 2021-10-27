package com.webank.wedatasphere.dss.appconn.dolphinscheduler.conversion;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.constant.Constant;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTask;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTaskParam;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;

/**
 * The type Node converter.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class NodeConverter {

    private static final Logger logger = LoggerFactory.getLogger(NodeConverter.class);

    public NodeConverter() {
    }

    /**
     * 将DSS中节点转为Dolphin Scheduler中节点task.
     *
     * @param schedulerNode
     *            the scheduler node
     * @return the dolphin scheduler task
     */
    public DolphinSchedulerTask conversion(DSSNode dssNode) {
        DolphinSchedulerTask task = new DolphinSchedulerTask();
        task.setId(dssNode.getId());
        task.setName(dssNode.getName());
        task.setPreTasks(dssNode.getDependencys());
        task.setType("SHELL");

        DolphinSchedulerTaskParam taskParams = new DolphinSchedulerTaskParam();

        try {
            HashMap<String, String> map = new HashMap<>();
            map.put(Constant.JOB_TYPE, "linkis");
            map.put(Constant.LINKIS_TYPE, dssNode.getNodeType());
            map.put(Constant.PROXY_USER, dssNode.getUserProxy());
            map.put(Constant.JOB_COMMAND, new Gson().toJson(dssNode.getJobContent()));
            map.put("params", new Gson().toJson(dssNode.getParams()));
            map.put("resources", new Gson().toJson(dssNode.getResources()));
            Map<String, Object> nodeParams = dssNode.getParams();
            if (nodeParams != null && !nodeParams.isEmpty()) {
                Object configuration = nodeParams.get("configuration");
                String confprefix = "node.conf.";
                ((Map<String, Map<String, Object>>)configuration)
                    .forEach((k, v) -> v.forEach((k2, v2) -> map.put(confprefix + k + "." + k2, v2.toString())));
            }

            // TODO 改为参数配置路径
            String dolphinScript =
                "java -jar /usr/local/dolphin/linkis-dolphinscheduler-client.jar '" + new Gson().toJson(map) + "'";
            taskParams.setRawScript(dolphinScript);
        } catch (Exception e) {
            logger.error("任务转换失败", e);
        }

        task.setParams(taskParams);
        return task;
    }

}
