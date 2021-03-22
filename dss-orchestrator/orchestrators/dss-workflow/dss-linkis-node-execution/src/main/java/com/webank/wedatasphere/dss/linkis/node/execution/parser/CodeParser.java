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

package com.webank.wedatasphere.dss.linkis.node.execution.parser;

import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionErrorException;
import com.webank.wedatasphere.dss.linkis.node.execution.job.CommonLinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils;
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource;
import com.webank.wedatasphere.linkis.filesystem.WorkspaceClientFactory;
import com.webank.wedatasphere.linkis.filesystem.request.WorkspaceClient;
import com.webank.wedatasphere.linkis.filesystem.response.ScriptFromBMLResponse;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by johnnwang on 2019/11/2.
 */
public class CodeParser implements JobParser {

    private final static Pattern pb = Pattern.compile("((project)|(flow)|(node))://[^\\s\"]+[$\\s]{0,1}", Pattern.CASE_INSENSITIVE);

    @Override
    public void parseJob(Job job) throws Exception{
        if (! ( job instanceof CommonLinkisJob) ) {
            return ;
        }
        CommonLinkisJob linkisAppjointJob  = (CommonLinkisJob) job;
        Map<String, Object> script = LinkisJobExecutionUtils.gson.fromJson(linkisAppjointJob.getCode(), new TypeToken<Map<String, Object>>() {}.getType());
        List<BMLResource> jobResourceList = linkisAppjointJob.getJobResourceList();
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

        Map<String, Object> executionParams = getExecutionParams(scriptResource, linkisAppjointJob);

        if (executionParams.get("executionCode") != null) {

            String executionCode = (String) executionParams.get("executionCode");
            linkisAppjointJob.getLogObj().info("************************************SUBMIT CODE************************************");
            linkisAppjointJob.getLogObj().info(executionCode);
            linkisAppjointJob.getLogObj().info("************************************SUBMIT CODE************************************");

            //get ResourceNames
            //ArrayList<String> resourceNames = getResourceNames(executionCode);

            //get ResourceList

            //ArrayList<BMLResource> bmlResourceArrayList = getResourcesByNames(resourceNames, linkisAppjointJob);

            //parsedCode
            linkisAppjointJob.setCode(executionCode);


           /* //put resources
            if (linkisAppjointJob.getRuntimeParams() != null) {
                linkisAppjointJob.getRuntimeParams().put("resources", bmlResourceArrayList);
            }*/
        }
        if (executionParams.get("params") != null && executionParams.get("params") instanceof Map) {
            if (linkisAppjointJob.getParams() != null) {
                linkisAppjointJob.getParams().putAll( (Map<String, Object>)executionParams.get("params"));
            }
        }

    }

    private Map<String, Object> getExecutionParams(BMLResource bmlResource,  CommonLinkisJob linkisAppjointJob) {
        Map<String, Object> map = new HashMap<>();
        WorkspaceClient client = WorkspaceClientFactory.getClient(linkisAppjointJob.getSubmitUser(), LinkisJobExecutionConfiguration.LINKIS_AUTHOR_USER_TOKEN.getValue(linkisAppjointJob.getJobProps()), LinkisJobExecutionConfiguration.LINKIS_URL.getValue(linkisAppjointJob.getJobProps()));
        ScriptFromBMLResponse response = client.requestOpenScriptFromBML(bmlResource.getResourceId(), bmlResource.getVersion(), bmlResource.getFileName());
        map.put("executionCode", response.scriptContent());
        map.put("params", response.metadata());
        return map;
    }

    private   ArrayList<String> getResourceNames(String code){
        ArrayList<String> bmlResourceNames = new ArrayList<String>();
        Matcher mb = pb.matcher(code);
        while (mb.find()) {
            bmlResourceNames.add(mb.group().trim());
        }
        return bmlResourceNames;
    }


    /**
     * 1.Find the project file used in the script
     * 2.Find the node file used in the script
     * 3.Recursively find the flow file used in the script
     * 4.Replace file name with prefixed name
     * @param resourceNames
     * @param linkisAppjointJob
     * @return
     */
    private  ArrayList<BMLResource> getResourcesByNames(ArrayList<String> resourceNames, CommonLinkisJob linkisAppjointJob) {

        ArrayList<BMLResource> bmlResourceArrayList = new ArrayList<>();

        String jobName = linkisAppjointJob.getJobName();
        String flowName = linkisAppjointJob.getSource().get("flowName");
        String projectName = linkisAppjointJob.getSource().get("projectName");


        List<BMLResource> projectResourceList = linkisAppjointJob.getProjectResourceList();


        List<BMLResource> jobResourceList = linkisAppjointJob.getJobResourceList();
        for (String resourceName : resourceNames) {
            String[] resourceNameSplit = resourceName.split("://");
            String prefix = resourceNameSplit[0].toLowerCase();
            String fileName = resourceNameSplit[1];
            BMLResource resource = null;
            String afterFileName = fileName;
            switch (prefix) {
                case "project":
                    resource = findResource(projectResourceList, fileName);
                    afterFileName = LinkisJobExecutionConfiguration.PROJECT_PREFIX + "_" + projectName + "_" + fileName;
                    break;
                case "flow":
                    resource = findFlowResource(linkisAppjointJob, fileName, flowName);
                    break;
                case "node":
                    resource = findResource(jobResourceList, fileName);
                    afterFileName = LinkisJobExecutionConfiguration.JOB_PREFIX + "_" + jobName + "_" + fileName;
                    break;
                default:
            }
            if (null == resource) {
                linkisAppjointJob.getLogObj().error("Failed to find the " + prefix + " resource file of " + fileName);
                throw new RuntimeException("Failed to find the " + prefix + " resource file of " + fileName);
            }
            if (!afterFileName.equals(fileName)) {
                resource.setFileName(afterFileName);
            }
            bmlResourceArrayList.add(resource);
        }
        return bmlResourceArrayList;
    }


    /**
     * Recursively find the flow file used in the script
     * Recursive exit condition is top-level flow
     *
     */
    private  BMLResource findFlowResource(CommonLinkisJob linkisAppjointJob, String fileName, String flowName) {

        String fullFlowName = "";
        Map<String, List<BMLResource>> fLowNameAndResources = linkisAppjointJob.getFlowNameAndResources();
        if (fLowNameAndResources == null){
            return null;
        }
        Optional<Map.Entry<String, List<BMLResource>>> first = fLowNameAndResources.entrySet().stream().filter(fLowNameAndResource -> fLowNameAndResource.getKey().endsWith(flowName + LinkisJobExecutionConfiguration.RESOURCES_NAME)).findFirst();

        if(first.isPresent()){
            fullFlowName = first.get().getKey();
            BMLResource resource = findResource(first.get().getValue(), fileName);
            if (resource != null) {
                resource.setFileName(flowName + "_" + fileName);
                return resource;
            }
        }

        String firstFlow = "flow." + flowName + LinkisJobExecutionConfiguration.RESOURCES_NAME;
        if (firstFlow.equals(fullFlowName)) {
            return null;
        }
        //getParentFlowName:flow.flows1.test.resources  return:flows1
        String parentFlowName = StringUtils.substringAfterLast(StringUtils.substringBefore(fullFlowName, "." + flowName
                + LinkisJobExecutionConfiguration.RESOURCES_NAME), ".");
        if (StringUtils.isEmpty(parentFlowName)) {
            return null;
        }

        return findFlowResource(linkisAppjointJob, fileName, parentFlowName);
    }


    private  String replaceCodeResourceNames(String code, ArrayList<String> resourceNameList, ArrayList<BMLResource> resourceList){
        if(resourceList.size() != resourceNameList.size()){
            throw new RuntimeException("Failed to parsed resource file");
        }

        String[] names = resourceNameList.toArray(new String[]{});

        String[] afterNames = new String[resourceList.size()];
        for (int i=0 ; i < afterNames.length ; i++){
            afterNames[i] = resourceList.get(i).getFileName();
        }
        return StringUtils.replaceEach(code, names, afterNames);
    }

    private   BMLResource findResource(List<BMLResource> resourceArrayList, String fileName){
        if(resourceArrayList != null && !resourceArrayList.isEmpty()) {
            for(BMLResource resource : resourceArrayList){
                if(resource.getFileName().equals(fileName)){
                    return resource;
                }
            }
        }
        return null;
    }
}
