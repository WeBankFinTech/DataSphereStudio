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

package com.webank.wedatasphere.dss.linkis.node.execution.service.impl;

import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.service.LinkisURLService;
import java.util.Map;


public class LinkisURLServiceImpl implements LinkisURLService {

    @Override
    public String getLinkisURL(Job job) {
        Map<String, String> props = job.getJobProps();
        if(LinkisJobExecutionConfiguration.isLinkis1_X(props)){
            return LinkisJobExecutionConfiguration.LINKIS_URL_1_X.getValue(props);
        } else {
            return LinkisJobExecutionConfiguration.LINKIS_URL.getValue(props);
        }
    }

    @Override
    public String getDefaultLinkisURL(Job job) {
        return LinkisJobExecutionConfiguration.LINKIS_URL_1_X.getValue(job.getJobProps());
    }
}
