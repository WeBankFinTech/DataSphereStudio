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

package com.webank.wedatasphere.dss.linkis.node.execution.parser;

import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource;
import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionErrorException;
import com.webank.wedatasphere.dss.linkis.node.execution.job.CommonLinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.service.LinkisURLService;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils;
import org.apache.linkis.filesystem.WorkspaceClientFactory;
import org.apache.linkis.filesystem.request.WorkspaceClient;
import org.apache.linkis.filesystem.response.ScriptFromBMLResponse;
import org.apache.linkis.protocol.utils.TaskUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class CodeParser implements JobParser {

    private static final Pattern pb = Pattern.compile("((project)|(flow)|(node))://[^\\s\"]+[$\\s]{0,1}", Pattern.CASE_INSENSITIVE);
    private volatile WorkspaceClient client1X = null;
    private volatile WorkspaceClient client0X = null;
    private final Object clientLocker = new Object();

    @Override
    public void parseJob(Job job) throws Exception{
        if (! (job instanceof CommonLinkisJob) ) {
            return ;
        }
        CommonLinkisJob linkisAppConnJob  = (CommonLinkisJob) job;
        Map<String, Object> script = LinkisJobExecutionUtils.gson.fromJson(linkisAppConnJob.getCode(), new TypeToken<Map<String, Object>>() {}.getType());
        List<BMLResource> jobResourceList = linkisAppConnJob.getJobResourceList();
        BMLResource scriptResource = null;
        if (script == null) {
            throw  new LinkisJobExecutionErrorException(90102,"Script is empty");
        }
        String fileName = (String) script.get("script");
        for(BMLResource bmlResource : jobResourceList){
            if(bmlResource.getFileName().equals(fileName)){
                scriptResource = bmlResource;
                break;
            }
        }
        if(null == scriptResource) {
            throw  new LinkisJobExecutionErrorException(90102,"Failed to get script resource");
        }
        getAndSetCode(scriptResource, linkisAppConnJob);
    }

    protected void getAndSetCode(BMLResource bmlResource,  LinkisJob linkisAppConnJob) {
        Map<String, Object> executionParams = getExecutionParams(bmlResource, linkisAppConnJob);
        if (executionParams.get("executionCode") != null) {
            String executionCode = (String) executionParams.get("executionCode");
            linkisAppConnJob.getLogObj().info("************************************SUBMIT CODE************************************");
            linkisAppConnJob.getLogObj().info(executionCode);
            linkisAppConnJob.getLogObj().info("************************************SUBMIT CODE************************************");
            //parsedCode
            linkisAppConnJob.setCode(executionCode);
        }
        if (executionParams.get("params") != null && executionParams.get("params") instanceof Map) {
            Map<String, Object> params = (Map<String, Object>) executionParams.get("params");
            if (!params.isEmpty()) {
                linkisAppConnJob.getLogObj().info("add params from external resources: " + params);
                // 为防止第三方传回 {"runtime":{},"startup":{},"variable":{},"special":{}}，将原params已有的runtime、startup等属性的map覆盖为空
                // 因此以下一个个去确认
                Map<String, Object> runtimeMap = TaskUtils.getRuntimeMap(params);
                if(!runtimeMap.isEmpty()) {
                    TaskUtils.addRuntimeMap(linkisAppConnJob.getParams(), runtimeMap);
                }
                Map<String, Object> specialMap = TaskUtils.getSpecialMap(params);
                if(!specialMap.isEmpty()) {
                    TaskUtils.addSpecialMap(linkisAppConnJob.getParams(), specialMap);
                }
                Map<String, Object> startupMap = TaskUtils.getStartupMap(params);
                if(!startupMap.isEmpty()) {
                    TaskUtils.addStartupMap(linkisAppConnJob.getParams(), startupMap);
                }
                Map<String, Object> variableMap = TaskUtils.getVariableMap(params);
                if(!variableMap.isEmpty()) {
                    TaskUtils.addVariableMap(linkisAppConnJob.getParams(), variableMap);
                }
            }
        }
        dealExecutionParams(linkisAppConnJob, executionParams);
    }

    protected void dealExecutionParams(LinkisJob linkisAppConnJob, Map<String, Object> executionParams) {}

    protected Map<String, Object> getExecutionParams(BMLResource bmlResource,  LinkisJob linkisAppConnJob) {
        Map<String, Object> map = new HashMap<>();
        ScriptFromBMLResponse response = getOrCreateWorkSpaceClient(linkisAppConnJob).requestOpenScriptFromBML(bmlResource.getResourceId(), bmlResource.getVersion(), bmlResource.getFileName());
        linkisAppConnJob.getLogObj().info("Get execution code from workspace client,bml resource id "+bmlResource.getResourceId()+", version is "+bmlResource.getVersion());
        map.put("executionCode", response.scriptContent());
        map.put("params", response.metadata());
        return map;
    }

    private WorkspaceClient getOrCreateWorkSpaceClient(LinkisJob linkisAppConnJob) {
        Map<String, String> props = linkisAppConnJob.getJobProps();
        if(LinkisJobExecutionConfiguration.isLinkis1_X(props)) {
            if (null == client1X) {
                synchronized (clientLocker) {
                    if (null == client1X) {
                        this.client1X = WorkspaceClientFactory.getClient(linkisAppConnJob.getSubmitUser(), LinkisJobExecutionConfiguration.LINKIS_AUTHOR_USER_TOKEN.getValue(linkisAppConnJob.getJobProps()),
                                LinkisURLService.Factory.getLinkisURLService().getLinkisURL(linkisAppConnJob));
                    }
                }
            }
            linkisAppConnJob.getLogObj().info("Use workspace client1X:"+LinkisURLService.Factory.getLinkisURLService().getLinkisURL(linkisAppConnJob));
            return client1X;
        }else{
            if (null == client0X) {
                synchronized (clientLocker) {
                    if (null == client0X) {
                        this.client0X = WorkspaceClientFactory.getClient(linkisAppConnJob.getSubmitUser(), LinkisJobExecutionConfiguration.LINKIS_AUTHOR_USER_TOKEN.getValue(linkisAppConnJob.getJobProps()),
                                LinkisURLService.Factory.getLinkisURLService().getLinkisURL(linkisAppConnJob));
                    }
                }
            }
            linkisAppConnJob.getLogObj().info("Use workspace client0X:"+LinkisURLService.Factory.getLinkisURLService().getLinkisURL(linkisAppConnJob));
            return client0X;
        }
    }

}
