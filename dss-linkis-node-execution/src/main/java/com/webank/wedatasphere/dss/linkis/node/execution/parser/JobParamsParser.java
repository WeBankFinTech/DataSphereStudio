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

package com.webank.wedatasphere.dss.linkis.node.execution.parser;

import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.linkis.node.execution.WorkflowContext;
import com.webank.wedatasphere.dss.linkis.node.execution.job.JobSignalKeyCreator;
import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.job.SignalSharedJob;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by johnnwang on 2019/11/3.
 */
public class JobParamsParser implements JobParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobParamsParser.class);
    private JobSignalKeyCreator signalKeyCreator;

    public JobSignalKeyCreator getSignalKeyCreator() {
        return signalKeyCreator;
    }

    public void setSignalKeyCreator(JobSignalKeyCreator signalKeyCreator) {
        this.signalKeyCreator = signalKeyCreator;
    }

    @Override
    public void parseJob(Job job) throws Exception {

        if (job instanceof LinkisJob) {

            LinkisJob linkisJob = (LinkisJob) job;

            linkisJob.getLogObj().info("Start to  put variable and configuration");
            //put variable
            Map<String, Object> flowVariables = linkisJob.getVariables();
            putParamsMap(job.getParams(), "variable", flowVariables);
            //put signal info
            Map<String, Object> sharedValue = WorkflowContext.getAppJointContext()
                    .getSubMapByPrefix(SignalSharedJob.PREFIX + this.getSignalKeyCreator().getSignalKeyByJob(job));
            if (sharedValue != null) {
                Collection<Object> values = sharedValue.values();
                for(Object value : values){
                    List<Map<String, Object>> list = LinkisJobExecutionUtils.gson.fromJson(value.toString(), List.class);
                    Map<String, Object> totalMap = new HashMap<>();
                    for (Map<String, Object> kv : list) {
                        totalMap.putAll(kv);
                    }
                    putParamsMap(job.getParams(), "variable", totalMap);
                }
            }
            // put configuration
            Map<String, Object> configuration = linkisJob.getConfiguration();
            putParamsMap(job.getParams(), "configuration", configuration);
            linkisJob.getLogObj().info("Finished to  put variable and configuration");
        }
    }

    private void putParamsMap(Map<String, Object> params, String key, Map<String, Object> value) {
        if (null == value) return;
        if (params.get(key) != null) {
            ((Map<String, Object>) params.get(key)).putAll(value);
        } else {
            params.put(key, value);
        }
    }
}
