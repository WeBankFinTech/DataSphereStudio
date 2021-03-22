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

package com.webank.wedatasphere.dss.linkis.node.execution.service.impl;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionErrorException;
import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.service.BuildJobAction;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.linkis.protocol.utils.TaskUtils;
import com.webank.wedatasphere.linkis.ujes.client.request.JobExecuteAction;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by johnnwang on 2019/11/3.
 */
public class BuildJobActionImpl implements BuildJobAction {

    Logger logger = LoggerFactory.getLogger(BuildJobActionImpl.class);
    private static BuildJobAction buildJobAction = new BuildJobActionImpl();

    private BuildJobActionImpl(){

    }

    public static BuildJobAction  getbuildJobAction(){
        return buildJobAction;
    }

    @Override
    public JobExecuteAction getJobAction(Job job) throws LinkisJobExecutionErrorException {

        enrichParams(job);
        Map<String,Object> labels = new HashMap<>();
        //todo get labels from job
        Object labelStr =job.getRuntimeParams().getOrDefault("labels",null);
        if(null != labelStr && labelStr.toString().toLowerCase().contains("prod")){
            labels.put("yarnCluster", "PROD-PROD");
        }else if(null != labelStr && labelStr.toString().toLowerCase().contains("test")){
            labels.put("yarnCluster", "TEST-TEST");
        }else {
            labels.put("yarnCluster", "DEV-DEV");
        }
        logger.info("Set job to label: " + labels.toString());
        TaskUtils.addLabelsMap(job.getParams(),labels);
        String code = job.getCode();
        if(StringUtils.isEmpty(code) || code.equalsIgnoreCase("null")){
            Gson gson = new Gson();
            code = gson.toJson(job.getParams());
        }
        JobExecuteAction.Builder builder = JobExecuteAction.builder().setCreator(LinkisJobExecutionConfiguration.LINKIS_JOB_CREATOR.getValue(job.getJobProps()))
                .addExecuteCode(code)
                .setEngineTypeStr(parseAppjointEngineType(job.getEngineType()))
                .setRunTypeStr(parseRunType(job.getEngineType(), job.getRunType()))
                .setUser(job.getUser())
                .setParams(job.getParams())
                .setRuntimeParams(job.getRuntimeParams());
        if(job instanceof LinkisJob){
            Map<String, Object> source = new HashMap<>();
            source.putAll(((LinkisJob) job).getSource());
            builder = builder.setSource(source);
        }
        return builder.build();
    }

    //TODO
    private String parseAppjointEngineType(String engineType){
        if(engineType.equalsIgnoreCase("appjoint")){
            return "appconn";
        }
        return engineType;
    }

    private String parseRunType(String engineType, String runType){
        if(engineType.equalsIgnoreCase("appjoint")){
            return "appconn";
        }else if(engineType.toLowerCase().contains("shell")){
            return "shell";
        }

        return runType;
    }

    private void enrichParams(Job job){
        job.getRuntimeParams().put("nodeType", job.getRunType());
    }


}
