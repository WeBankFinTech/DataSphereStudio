package com.webank.wedatasphere.dss.appconn.dolphinscheduler.linkisjob;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.constant.Constant;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerSchedulerNode;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTask;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerTaskParam;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.LinkisAzkabanReadNode;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.linkis.filesystem.bml.BMLHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public DolphinSchedulerTask conversion(DolphinSchedulerSchedulerNode schedulerNode) {
        return baseConversion(schedulerNode);
    }

    public String conversion(LinkisAzkabanReadNode schedulerNode) {
        String tmpConversion = "";//baseConversion(schedulerNode);
        String[] shareNodeArray = schedulerNode.getShareNodeIds();
        String readNodePrefix = "read.nodes = ";
        String shareNodeIds = StringUtils.join(shareNodeArray, ",");
        return tmpConversion + readNodePrefix + shareNodeIds + "\n";
    }

    private DolphinSchedulerTask baseConversion(DolphinSchedulerSchedulerNode schedulerNode) {
        DSSNodeDefault dssNode = (DSSNodeDefault) schedulerNode.getDSSNode();

        DolphinSchedulerTask task = new DolphinSchedulerTask();
        task.setId(schedulerNode.getId());
        task.setName(schedulerNode.getName());
        task.setPreTasks(schedulerNode.getDSSNode().getDependencys());
        // TODO: 2021/4/29 先固定为Shell，后续改进
        task.setType("SHELL");
        // TODO: 2021/4/29 获取描述信息
        // task.setDescription(dssNode.getDesc());

        DolphinSchedulerTaskParam params = new DolphinSchedulerTaskParam();
        Resource resource = dssNode.getResources().get(0);
        // 从资源中获取脚本内容
        Map<String, Object> query = bmlHelper.query(schedulerNode.getUser(), resource.getResourceId(), resource.getVersion());
        InputStream inputStream = (InputStream) query.get("stream");
        try {
            String script = IOUtils.toString(inputStream);
            HashMap<String, String> map = new HashMap<>();
            map.put("linkisType", schedulerNode.getNodeType());
            map.put("proxyUser", schedulerNode.getDSSNode().getUserProxy());
            map.put(Constant.JOB_COMMAND, new Gson().toJson(dssNode.getJobContent()));
            map.put("params", new Gson().toJson(dssNode.getParams()));
            map.put("resources", new Gson().toJson(dssNode.getResources()));
            String rawScript = "java -jar /usr/local/dolphin/linkis-dolphinscheduler-client.jar '" + new Gson().toJson(map) + "'";
            params.setRawScript(rawScript);
        } catch (IOException e) {
            logger.error("获取节点{}脚本内容出错", e, dssNode.getName());
        }
        task.setParams(params);

        return task;
    }
}
