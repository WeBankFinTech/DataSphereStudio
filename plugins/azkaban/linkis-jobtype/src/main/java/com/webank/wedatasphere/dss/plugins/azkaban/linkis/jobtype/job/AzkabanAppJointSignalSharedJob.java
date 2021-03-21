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

package com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.job;

import com.webank.wedatasphere.dss.linkis.node.execution.job.JobSignalKeyCreator;
import com.webank.wedatasphere.dss.linkis.node.execution.job.SignalSharedJob;
import com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.conf.LinkisJobTypeConf;

import java.util.Map;

/**
 * Created by johnnwang on 2019/11/14.
 */
public class AzkabanAppJointSignalSharedJob extends AzkabanAppJointLinkisJob implements SignalSharedJob {


    private JobSignalKeyCreator signalKeyCreator;

    @Override
    public JobSignalKeyCreator getSignalKeyCreator() {
        return this.signalKeyCreator;
    }

    @Override
    public void setSignalKeyCreator(JobSignalKeyCreator signalKeyCreator) {
        this.signalKeyCreator = signalKeyCreator;
    }

    @Override
    public String getMsgSaveKey() {
        Map<String, Object> configuration = this.getConfiguration();
        Object map = configuration.get("runtime");
        if (map != null && map instanceof Map) {
            Map<String, Object> runtime = (Map<String, Object>) map;
            Object msgValue = runtime.get(LinkisJobTypeConf.MSG_SAVE_KEY);
            if (null != msgValue) {
                return msgValue.toString();
            }
        }
        return null;
    }
}
