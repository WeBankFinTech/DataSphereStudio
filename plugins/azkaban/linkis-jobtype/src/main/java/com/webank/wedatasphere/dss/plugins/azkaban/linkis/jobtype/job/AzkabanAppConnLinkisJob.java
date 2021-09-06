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

package com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.job;

import com.webank.wedatasphere.dss.linkis.node.execution.job.AbstractAppConnLinkisJob;
import com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.conf.LinkisJobTypeConf;


public class AzkabanAppConnLinkisJob extends AbstractAppConnLinkisJob {


    @Override
    public String getSubmitUser() {
        return getJobProps().get(LinkisJobTypeConf.FLOW_SUBMIT_USER);
    }


    @Override
    public String getUser() {
        return getJobProps().get(LinkisJobTypeConf.PROXY_USER);
    }

    @Override
    public String getJobName() {
        return getJobProps().get(LinkisJobTypeConf.JOB_ID);
    }


}
