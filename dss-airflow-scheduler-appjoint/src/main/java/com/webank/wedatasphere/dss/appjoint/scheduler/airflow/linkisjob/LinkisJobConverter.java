package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.linkisjob;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.constant.AirflowConstant;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.LinkisAirflowReadNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.LinkisAirflowSchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.entity.LinkisAirflowShareNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.constant.SchedulerAppJointConstant;
import org.apache.commons.lang.StringUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Xudong Zhang on 2020/8/6.
 */

public class LinkisJobConverter {

    public LinkisJobConverter(){
        LinkisJobTuning[] linkisJobTunings = {new AirflowSubFlowJobTuning()};
        this.linkisJobTunings = linkisJobTunings;
    }
    private LinkisJobTuning[] linkisJobTunings;

    public String conversion(LinkisAirflowSchedulerNode schedulerNode){
        return baseConversion(schedulerNode);
    }

    public String conversion(LinkisAirflowReadNode schedulerNode){
        String tmpConversion = baseConversion(schedulerNode);
        String[] shareNodeArray =  schedulerNode.getShareNodeIds();
        String readNodePrefix = "read.nodes = ";
        String shareNodeIds = StringUtils.join(shareNodeArray, ",");
        return tmpConversion + readNodePrefix + shareNodeIds + "\n";
    }

    public String conversion(LinkisAirflowShareNode schedulerNode){
        String tmpConversion = baseConversion(schedulerNode);
        String   shareNodePrefix = "share.num  = ";
        String shareNum = Integer.toString((schedulerNode).getShareTimes());
        return tmpConversion + shareNodePrefix + shareNum + "\n";
    }

    private String baseConversion(LinkisAirflowSchedulerNode schedulerNode){
        LinkisJob job = new LinkisJob();
        job.setConf(new HashMap<String,String>());
        job.setName(schedulerNode.getName());
        convertHead(schedulerNode,job);
        convertDependencies(schedulerNode,job);
        convertProxyUser(schedulerNode,job);
        convertConfiguration(schedulerNode,job);
        convertJobCommand(schedulerNode,job);
        Arrays.stream(linkisJobTunings).forEach(t ->{
            if(t.ifJobCantuning(schedulerNode.getNodeType())) t.tuningJob(job);
        });
        return convertJobtoString(job);
    }

    private String convertJobtoString(LinkisJob job){
        HashMap<String, String> map = new HashMap<>();
        map.put(AirflowConstant.JOB_TYPE,job.getType());
        map.put(AirflowConstant.LINKIS_TYPE,job.getLinkistype());
        map.put(AirflowConstant.ZAKABAN_DEPENDENCIES_KEY,job.getDependencies());
        map.put(SchedulerAppJointConstant.PROXY_USER,job.getProxyUser());
        map.put(AirflowConstant.JOB_COMMAND,job.getCommand());
        map.putAll(job.getConf());
        StringBuilder stringBuilder = new StringBuilder();
        map.forEach((k,v)->{
            if(v != null) stringBuilder.append(k).append("=").append(v).append("\n");
        });
        return stringBuilder.toString();
    }

    private void convertHead(LinkisAirflowSchedulerNode schedulerNode, LinkisJob job){
        job.setType("linkis");
        job.setLinkistype(schedulerNode.getNodeType());
    }

    private void convertDependencies(LinkisAirflowSchedulerNode schedulerNode, LinkisJob job){
        List<String> dependencys = schedulerNode.getDssNode().getDependencys();
        if(dependencys != null && !dependencys.isEmpty()) {
            StringBuilder dependencies = new StringBuilder();
            dependencys.forEach(d ->dependencies.append(d + ","));
            job.setDependencies(dependencies.substring(0,dependencies.length()-1));
        }
    }

    private void convertProxyUser(LinkisAirflowSchedulerNode schedulerNode, LinkisJob job){
        String userProxy = schedulerNode.getDssNode().getUserProxy();
        if(!StringUtils.isEmpty(userProxy)) job.setProxyUser(userProxy);
    }

    private void convertConfiguration(LinkisAirflowSchedulerNode schedulerNode, LinkisJob job){
        Map<String, Object> params = schedulerNode.getDssNode().getParams();
        if (params != null && !params.isEmpty()) {
            Object configuration = params.get("configuration");
            String confprefix = "node.conf.";
            ((Map<String,Map<String,Object>>)configuration).forEach((k,v)->v.forEach((k2,v2)->job.getConf().put(confprefix + k + "." + k2,v2.toString())));
        }

    }

    private void convertJobCommand(LinkisAirflowSchedulerNode schedulerNode, LinkisJob job){
        Map<String, Object> jobContent = schedulerNode.getDssNode().getJobContent();
        if(jobContent != null) {
            jobContent.remove("jobParams");
            job.setCommand(new Gson().toJson(jobContent));
        }
    }
}
