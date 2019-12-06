/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.linkis.node.execution.service.impl;

import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionErrorException;
import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.service.BuildJobAction;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.linkis.ujes.client.request.JobExecuteAction;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by peacewong on 2019/11/3.
 */
public class BuildJobActionImpl implements BuildJobAction {

    private static BuildJobAction buildJobAction = new BuildJobActionImpl();

    private BuildJobActionImpl(){

    }

    public static BuildJobAction  getbuildJobAction(){
        return buildJobAction;
    }

    @Override
    public JobExecuteAction getJobAction(Job job) throws LinkisJobExecutionErrorException {

        JobExecuteAction.Builder builder = JobExecuteAction.builder().setCreator(LinkisJobExecutionConfiguration.LINKIS_JOB_CREATOR.getValue(job.getJobProps()))
                .addExecuteCode(job.getCode())
                .setEngineTypeStr(job.getEngineType())
                .setRunTypeStr(job.getRunType())
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


}
