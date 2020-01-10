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

package com.webank.wedatasphere.dss.flow.execution.entrance.job;

import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.job.JobSignalKeyCreator;
import com.webank.wedatasphere.dss.linkis.node.execution.job.SignalSharedJob;

public class FlowExecutionJobSignalKeyCreator implements JobSignalKeyCreator {

    @Override
    public String getSignalKeyByJob(Job job) {
        String projectId = job.getJobProps().get(FlowExecutionEntranceConfiguration.PROJECT_NAME());
        String flowId = job.getJobProps().get(FlowExecutionEntranceConfiguration.FLOW_NAME());
        String flowExecId = job.getJobProps().get(FlowExecutionEntranceConfiguration.FLOW_EXEC_ID());
        return projectId + "." + flowId + "." + flowExecId;
    }

    @Override
    public String getSignalKeyBySignalSharedJob(SignalSharedJob job) {
        return getSignalKeyByJob((Job)job);
    }
}
