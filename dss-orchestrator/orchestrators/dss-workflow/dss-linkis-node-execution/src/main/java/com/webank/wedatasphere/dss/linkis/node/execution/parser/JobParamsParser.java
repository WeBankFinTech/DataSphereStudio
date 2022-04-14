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

import com.webank.wedatasphere.dss.linkis.node.execution.job.AppConnLinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob;
import org.apache.linkis.protocol.utils.TaskUtils;

import java.util.Map;


public class JobParamsParser implements JobParser {

    @Override
    public void parseJob(Job job) {

        if (job instanceof LinkisJob) {
            LinkisJob linkisJob = (LinkisJob) job;
            linkisJob.getLogObj().info("Start to  put variable and configuration");
            //put variable
            Map<String, Object> flowVariables = linkisJob.getVariables();
            putParamsMap(job.getParams(), "variable", flowVariables);
            // put configuration
            Map<String, Object> configuration = linkisJob.getConfiguration();
            putParamsMap(job.getParams(), "configuration", configuration);

            linkisJob.getLogObj().info("Finished to  put variable and configuration");
        }
        // set the variable to runtimeMap since AppConn may need it.
        if(job instanceof AppConnLinkisJob) {
            job.getRuntimeParams().put("variables", TaskUtils.getVariableMap(job.getParams()));
        }
    }

    private void putParamsMap(Map<String, Object> params, String key, Map<String, Object> value) {
        if (null == value) {
            return;
        }
        if (params.get(key) != null) {
            ((Map<String, Object>) params.get(key)).putAll(value);
        } else {
            params.put(key, value);
        }
    }
}
