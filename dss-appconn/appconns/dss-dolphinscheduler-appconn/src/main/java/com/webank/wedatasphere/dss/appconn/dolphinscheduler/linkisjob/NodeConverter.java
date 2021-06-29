package com.webank.wedatasphere.dss.appconn.dolphinscheduler.linkisjob;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.constant.Constant;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerNode;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTask;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTaskParam;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.LinkisAzkabanReadNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.linkis.filesystem.bml.BMLHelper;

/**
 * The type Node converter.
 *
 * @author yuxin.yuan
 * @date 2021/04/29
 */
public class NodeConverter {

    private static final Logger logger = LoggerFactory.getLogger(NodeConverter.class);

    //    private LinkisJobTuning[] linkisJobTunings;

    private BMLHelper bmlHelper;

    public NodeConverter() {
        //        LinkisJobTuning[] linkisJobTunings = {new AzkabanSubFlowJobTuning()};
        //        this.linkisJobTunings = linkisJobTunings;
        bmlHelper = new BMLHelper();
    }

    /**
     * 将DSS中节点SchedulerNode转为Dolphin Scheduler中节点task.
     *
     * @param schedulerNode the scheduler node
     * @return the dolphin scheduler task
     */
    public DolphinSchedulerTask conversion(DolphinSchedulerNode schedulerNode) {
        return baseConversion(schedulerNode);
    }

    public String conversion(LinkisAzkabanReadNode schedulerNode) {
        String tmpConversion = "";//baseConversion(schedulerNode);
        String[] shareNodeArray = schedulerNode.getShareNodeIds();
        String readNodePrefix = "read.nodes = ";
        String shareNodeIds = StringUtils.join(shareNodeArray, ",");
        return tmpConversion + readNodePrefix + shareNodeIds + "\n";
    }

    private DolphinSchedulerTask baseConversion(DolphinSchedulerNode schedulerNode) {
        DSSNodeDefault dssNode = (DSSNodeDefault) schedulerNode.getDSSNode();

        DolphinSchedulerTask task = new DolphinSchedulerTask();
        task.setId(schedulerNode.getId());
        task.setName(schedulerNode.getName());
        task.setPreTasks(schedulerNode.getDSSNode().getDependencys());
        task.setType("SHELL");
        // task.setDescription(dssNode.getDesc());

        DolphinSchedulerTaskParam taskParams = new DolphinSchedulerTaskParam();

        try {
            HashMap<String, String> map = new HashMap<>();
            map.put(Constant.JOB_TYPE, "linkis");
            map.put(Constant.LINKIS_TYPE, schedulerNode.getNodeType());
            map.put(Constant.PROXY_USER, schedulerNode.getDSSNode().getUserProxy());
            map.put(Constant.JOB_COMMAND, new Gson().toJson(dssNode.getJobContent()));
            map.put("params", new Gson().toJson(dssNode.getParams()));
            map.put("resources", new Gson().toJson(dssNode.getResources()));
            Map<String, Object> nodeParams = dssNode.getParams();
            if (nodeParams != null && !nodeParams.isEmpty()) {
                Object configuration = nodeParams.get("configuration");
                String confprefix = "node.conf.";
                ((Map<String, Map<String, Object>>) configuration).forEach((k, v) -> v.forEach((k2, v2) -> map.put(confprefix + k + "." + k2, v2.toString())));
            }

            //TODO 改为参数配置路径
            String dolphinScript = "java -jar /usr/local/dolphin/linkis-dolphinscheduler-client.jar '" + new Gson().toJson(map) + "'";
            taskParams.setRawScript(dolphinScript);
        } catch (Exception e) {
            logger.error("任务转换失败", e);
        }

        task.setParams(taskParams);
        return task;
    }
}
