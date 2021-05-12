package com.webank.wedatasphere.dss.appconn.dolphinscheduler.linkisjob;

import com.google.gson.Gson;
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
        DSSNodeDefault dssNode = (DSSNodeDefault)schedulerNode.getDSSNode();

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
        Map<String, Object> query = bmlHelper.query(schedulerNode.getUser(), resource.getResourceId(),
            resource.getVersion());
        InputStream inputStream = (InputStream)query.get("stream");
        try {
            String script = IOUtils.toString(inputStream);
            params.setRawScript(script);
        } catch (IOException e) {
            logger.error("获取节点{}脚本内容出错", e, dssNode.getName());
        }
        task.setParams(params);

        return task;
    }

    //    private String convertJobtoString(LinkisJob job) {
    //        HashMap<String, String> map = new HashMap<>();
    //        map.put(Constant.JOB_TYPE, job.getType());
    //        map.put(Constant.LINKIS_TYPE, job.getLinkistype());
    //        map.put(Constant.ZAKABAN_DEPENDENCIES_KEY, job.getDependencies());
    //        map.put(SchedulerAppConnConstant.PROXY_USER, job.getProxyUser());
    //        map.put(Constant.JOB_COMMAND, job.getCommand());
    //        map.putAll(job.getConf());
    //        StringBuilder stringBuilder = new StringBuilder();
    //        map.forEach((k, v) -> {
    //            if (v != null) {
    //                stringBuilder.append(k).append("=").append(v).append("\n");
    //            }
    //        });
    //        return stringBuilder.toString();
    //}

    private void convertHead(DolphinSchedulerSchedulerNode schedulerNode, LinkisJob job) {
        job.setType("linkis");
        //        job.setLinkistype(schedulerNode.getNodeType());
    }

    private void convertDependencies(DolphinSchedulerSchedulerNode schedulerNode, LinkisJob job) {
        List<String> dependencys = schedulerNode.getDSSNode().getDependencys();
        if (dependencys != null && !dependencys.isEmpty()) {
            StringBuilder dependencies = new StringBuilder();
            dependencys.forEach(d -> dependencies.append(d + ","));
            job.setDependencies(dependencies.substring(0, dependencies.length() - 1));
        }
    }

    private void convertProxyUser(DolphinSchedulerSchedulerNode schedulerNode, LinkisJob job) {
        String userProxy = schedulerNode.getDSSNode().getUserProxy();
        if (!StringUtils.isEmpty(userProxy)) {
            job.setProxyUser(userProxy);
        }
    }

    private void convertConfiguration(DolphinSchedulerSchedulerNode schedulerNode, LinkisJob job) {
        Map<String, Object> params = schedulerNode.getDSSNode().getParams();
        if (params != null && !params.isEmpty()) {
            Object configuration = params.get("configuration");
            String confprefix = "node.conf.";
            ((Map<String, Map<String, Object>>)configuration).forEach((k, v) -> {
                if (null != v) {
                    v.forEach((k2, v2) -> {
                        if (null != v2) {
                            job.getConf().put(confprefix + k + "." + k2, v2.toString());
                        }
                    });
                }
            });
        }

    }

    private void convertJobCommand(DolphinSchedulerSchedulerNode schedulerNode, LinkisJob job) {
        Map<String, Object> jobContent = schedulerNode.getDSSNode().getJobContent();
        if (jobContent != null) {
            jobContent.remove("jobParams");
            job.setCommand(new Gson().toJson(jobContent));
        }
    }
}
