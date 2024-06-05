/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.schedulis.linkisjob;

import com.webank.wedatasphere.dss.appconn.scheduler.utils.SchedulerConf;
import com.webank.wedatasphere.dss.appconn.schedulis.conf.AzkabanConf;
import com.webank.wedatasphere.dss.appconn.schedulis.constant.AzkabanConstant;
import com.webank.wedatasphere.dss.appconn.schedulis.conversion.NodeConverter;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.workflow.core.constant.WorkflowConstant;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowNode;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkisJobConverter implements NodeConverter {

    private LinkisJobTuning[] linkisJobTunings;

    public LinkisJobConverter(){
        this.linkisJobTunings = new LinkisJobTuning[]{new AzkabanSubFlowJobTuning()};
    }

    @Override
    public String conversion(WorkflowNode workflowNode){
        return baseConversion(workflowNode);
    }

    private String baseConversion(WorkflowNode workflowNode){
        LinkisJob job = new LinkisJob();
        job.setConf(new HashMap<>());
        job.setName(workflowNode.getName());
        job.setComment(workflowNode.getDSSNode().getDesc());
        convertHead(workflowNode,job);
        convertDependencies(workflowNode,job);
        convertProxyUser(workflowNode,job);
        convertConfiguration(workflowNode,job);
        convertJobCommand(workflowNode,job);
        Arrays.stream(linkisJobTunings).forEach(t ->{
            if(t.ifJobCantuning(workflowNode.getNodeType())) {
                t.tuningJob(job);
            }
        });
        return convertJobToString(job);
    }

    private String convertJobToString(LinkisJob job){
        HashMap<String, String> map = new HashMap<>(8);
        map.put(AzkabanConstant.LINKIS_VERSION, AzkabanConf.LINKIS_VERSION.getValue());
        map.put(AzkabanConstant.JOB_TYPE,job.getType());
        map.put(AzkabanConstant.LINKIS_TYPE,job.getLinkistype());
        map.put(AzkabanConstant.ZAKABAN_DEPENDENCIES_KEY,job.getDependencies());
        map.put(WorkflowConstant.PROXY_USER,job.getProxyUser());
        map.put(AzkabanConstant.JOB_COMMAND,job.getCommand());
        map.put(AzkabanConstant.JOB_COMMENT,job.getComment());
        map.put(AzkabanConstant.AUTO_DISABLED,job.getAutoDisabled());
        Map<String, Object> labels = new HashMap<>(1);
        labels.put("route", SchedulerConf.JOB_LABEL.getValue());
        map.put(AzkabanConstant.JOB_LABELS, DSSCommonUtils.COMMON_GSON.toJson(labels));
        map.putAll(job.getConf());
        StringBuilder stringBuilder = new StringBuilder();
        map.forEach((k,v)->{
            if(v != null) {
                //for value contains "\n"
                v = v.replace("\n", ";");
                stringBuilder.append(k).append("=").append(v).append("\n");
            }
        });
        return stringBuilder.toString();
    }

    private void convertHead(WorkflowNode workflowNode, LinkisJob job){
        job.setType("linkis");
        job.setLinkistype(workflowNode.getNodeType());
    }

    private void convertDependencies(WorkflowNode workflowNode, LinkisJob job){
        List<String> dependencys = workflowNode.getDSSNode().getDependencys();
        if(dependencys != null && !dependencys.isEmpty()) {
            StringBuilder dependencies = new StringBuilder();
            dependencys.forEach(d -> dependencies.append(d).append(","));
            job.setDependencies(dependencies.substring(0,dependencies.length()-1));
        }
    }

    private void convertProxyUser(WorkflowNode workflowNode, LinkisJob job){
        String userProxy = workflowNode.getDSSNode().getUserProxy();
        if(!StringUtils.isEmpty(userProxy)) {
            job.setProxyUser(userProxy);
        }
    }

    private void convertConfiguration(WorkflowNode workflowNode, LinkisJob job){
        Map<String, Object> params = workflowNode.getDSSNode().getParams();
        if (params != null && !params.isEmpty()) {
            Map<String, Map<String,Object>> configuration = (Map<String, Map<String, Object>>) params.get("configuration");
            String confprefix = "node.conf.";
            configuration.forEach((k,v)-> {
                if(null!=v) {
                    v.forEach((k2, v2) -> {
                        if(AzkabanConstant.AUTO_DISABLED.equals(k2) && null !=v2){job.setAutoDisabled(v2.toString());}
                        else if(null !=v2) {job.getConf().put(confprefix + k + "." + k2, v2.toString());}
                    });
                }
            });
        }

    }

    private void convertJobCommand(WorkflowNode workflowNode, LinkisJob job){
        Map<String, Object> jobContent = workflowNode.getDSSNode().getJobContent();
        if(jobContent != null) {
            jobContent.remove("jobParams");
            job.setCommand(DSSCommonUtils.COMMON_GSON.toJson(jobContent));
        }
    }
}
