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

import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.job.ReadJob;

/**
 * Created by johnnwang on 2019/11/3.
 */
public class JobRuntimeParamsParser implements JobParser{

    @Override
    public void parseJob(Job job) throws Exception{
        if(job instanceof LinkisJob) {
            job.getRuntimeParams().put(LinkisJobExecutionConfiguration.LINKIS_SUBMIT_USER,((LinkisJob) job).getSubmitUser());
        }
        if (job instanceof ReadJob) {
            //put the shared nods be  read node need
            job.getRuntimeParams()
                    .put(LinkisJobExecutionConfiguration.WORKFLOW_SHARED_NODES_JOBIDS, ((ReadJob) job).getSharedNodesInfo());
        }
    }
}
