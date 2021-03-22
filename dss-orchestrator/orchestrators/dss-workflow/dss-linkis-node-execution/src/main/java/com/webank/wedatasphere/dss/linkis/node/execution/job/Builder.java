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

package com.webank.wedatasphere.dss.linkis.node.execution.job;

import com.webank.wedatasphere.dss.linkis.node.execution.WorkflowContext;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionErrorException;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by johnnwang on 2019/11/13.
 */
public abstract class Builder {

    protected abstract String getJobType();

    protected abstract LinkisJob creatLinkisJob(boolean isLinkisType);

    protected abstract void fillJobInfo(Job job);

    protected abstract void fillLinkisJobInfo(LinkisJob linkisJob);

    protected abstract void fillCommonLinkisJobInfo(CommonLinkisJob linkisAppjointJob);


    public Job build() throws Exception {

        LinkisJob job = null;
        String jobType = getJobType();
        String[] jobTypeSplit = jobType.split("\\.");
        if (jobTypeSplit.length < 3) {
            throw new LinkisJobExecutionErrorException(90100, "This is not Linkis job type,this jobtype is " + jobType);
        }
        String engineType = jobTypeSplit[1];
        //delete linkis.engineType
        String runType = StringUtils.substringAfterLast(jobType, jobTypeSplit[0] + "." + jobTypeSplit[1] + ".");

        if (LinkisJobExecutionConfiguration.LINKIS_CONTROL_EMPTY_NODE.equalsIgnoreCase(jobType)) {
            job = new AbstractCommonLinkisJob() {
                @Override
                public String getSubmitUser() {
                    return null;
                }

                @Override
                public String getUser() {
                    return null;
                }

                @Override
                public String getJobName() {
                    return null;
                }
            };

            job.setJobType(JobTypeEnum.EmptyJob);
            return job;
        }
        //update by peaceWong
        if (LinkisJobExecutionUtils.isCommonAppjointJob(engineType)) {
            job = creatLinkisJob(false);
            job.setJobType(JobTypeEnum.CommonJob);
        } else {
            job = creatLinkisJob(true);
            job.setJobType(JobTypeEnum.CommonJob);
            fillCommonLinkisJobInfo((CommonLinkisJob) job);
        }

        job.setEngineType(engineType);
        job.setRunType(runType);
        fillJobInfo(job);
        fillLinkisJobInfo(job);

        return job;
    }


    private Map<String, Object> getSharedNodesAndJobId(ReadJob job) {
        Map<String, Object> map = new HashMap<>();
        String[] sharedIds = job.getShareNodeIds();
        if (sharedIds == null) {
            return map;
        }
        for (String nodeId : sharedIds) {
            map.put(nodeId, WorkflowContext.getAppJointContext().getValue(job.getSharedKey(nodeId)));
        }
        return map;
    }

}
