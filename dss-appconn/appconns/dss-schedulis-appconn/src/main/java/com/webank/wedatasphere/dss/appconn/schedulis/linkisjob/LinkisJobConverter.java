/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.schedulis.linkisjob;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.schedulis.constant.AzkabanConstant;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.LinkisAzkabanReadNode;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.LinkisAzkabanSchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.LinkisAzkabanShareNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.constant.SchedulerAppConnConstant;
import org.apache.commons.lang.StringUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by v_wbjftang on 2019/11/1.
 */

public class LinkisJobConverter {

    public LinkisJobConverter(){
        LinkisJobTuning[] linkisJobTunings = {new AzkabanSubFlowJobTuning()};
        this.linkisJobTunings = linkisJobTunings;
    }
    private LinkisJobTuning[] linkisJobTunings;

    public String conversion(LinkisAzkabanSchedulerNode schedulerNode){
        return baseConversion(schedulerNode);
    }

    public String conversion(LinkisAzkabanReadNode schedulerNode){
        String tmpConversion = baseConversion(schedulerNode);
        String[] shareNodeArray =  schedulerNode.getShareNodeIds();
        String readNodePrefix = "read.nodes = ";
        String shareNodeIds = StringUtils.join(shareNodeArray, ",");
        return tmpConversion + readNodePrefix + shareNodeIds + "\n";
    }

    public String conversion(LinkisAzkabanShareNode schedulerNode){
        String tmpConversion = baseConversion(schedulerNode);
        String   shareNodePrefix = "share.num  = ";
        String shareNum = Integer.toString((schedulerNode).getShareTimes());
        return tmpConversion + shareNodePrefix + shareNum + "\n";
    }

    private String baseConversion(LinkisAzkabanSchedulerNode schedulerNode){
        LinkisJob job = new LinkisJob();
        job.setConf(new HashMap<String,String>());
        job.setName(schedulerNode.getName());
        convertHead(schedulerNode,job);
        convertDependencies(schedulerNode,job);
        convertProxyUser(schedulerNode,job);
        convertConfiguration(schedulerNode,job);
        convertJobCommand(schedulerNode,job);
        Arrays.stream(linkisJobTunings).forEach(t ->{
            if(t.ifJobCantuning(schedulerNode.getNodeType())) {
                t.tuningJob(job);
            }
        });
        return convertJobtoString(job);
    }

    private String convertJobtoString(LinkisJob job){
        HashMap<String, String> map = new HashMap<>();
        map.put(AzkabanConstant.JOB_TYPE,job.getType());
        map.put(AzkabanConstant.LINKIS_TYPE,job.getLinkistype());
        map.put(AzkabanConstant.ZAKABAN_DEPENDENCIES_KEY,job.getDependencies());
        map.put(SchedulerAppConnConstant.PROXY_USER,job.getProxyUser());
        map.put(AzkabanConstant.JOB_COMMAND,job.getCommand());
        map.putAll(job.getConf());
        StringBuilder stringBuilder = new StringBuilder();
        map.forEach((k,v)->{
            if(v != null) {
                stringBuilder.append(k).append("=").append(v).append("\n");
            }
        });
        return stringBuilder.toString();
    }

    private void convertHead(LinkisAzkabanSchedulerNode schedulerNode,LinkisJob job){
        job.setType("linkis");
        job.setLinkistype(schedulerNode.getNodeType());
    }

    private void convertDependencies(LinkisAzkabanSchedulerNode schedulerNode,LinkisJob job){
        List<String> dependencys = schedulerNode.getDSSNode().getDependencys();
        if(dependencys != null && !dependencys.isEmpty()) {
            StringBuilder dependencies = new StringBuilder();
            dependencys.forEach(d ->dependencies.append(d + ","));
            job.setDependencies(dependencies.substring(0,dependencies.length()-1));
        }
    }

    private void convertProxyUser(LinkisAzkabanSchedulerNode schedulerNode,LinkisJob job){
        String userProxy = schedulerNode.getDSSNode().getUserProxy();
        if(!StringUtils.isEmpty(userProxy)) {
            job.setProxyUser(userProxy);
        }
    }

    private void convertConfiguration(LinkisAzkabanSchedulerNode schedulerNode,LinkisJob job){
        Map<String, Object> params = schedulerNode.getDSSNode().getParams();
        if (params != null && !params.isEmpty()) {
            Object configuration = params.get("configuration");
            String confprefix = "node.conf.";
            ((Map<String,Map<String,Object>>)configuration).forEach((k,v)->
            {
                if(null!=v) {
                    v.forEach((k2, v2) -> {
                        if(null !=v2) {job.getConf().put(confprefix + k + "." + k2, v2.toString());}
                    });
                }
            });
        }

    }

    private void convertJobCommand(LinkisAzkabanSchedulerNode schedulerNode,LinkisJob job){
        Map<String, Object> jobContent = schedulerNode.getDSSNode().getJobContent();
        if(jobContent != null) {
            jobContent.remove("jobParams");
            job.setCommand(new Gson().toJson(jobContent));
        }
    }
}
