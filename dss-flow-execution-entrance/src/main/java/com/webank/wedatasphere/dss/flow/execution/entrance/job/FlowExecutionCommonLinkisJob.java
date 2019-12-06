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
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource;
import com.webank.wedatasphere.dss.linkis.node.execution.job.AbstractCommonLinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.CommonLinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.JobTypeEnum;
import com.webank.wedatasphere.dss.linkis.node.execution.log.LinkisJobExecutionLog;
import com.webank.wedatasphere.linkis.ujes.client.response.JobExecuteResult;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by peacewong on 2019/11/3.
 */
public class FlowExecutionCommonLinkisJob extends AbstractCommonLinkisJob {


    @Override
    public String getSubmitUser() {
        return getJobProps().get(FlowExecutionEntranceConfiguration.FLOW_SUBMIT_USER());
    }


    @Override
    public String getUser() {
        return getJobProps().get(FlowExecutionEntranceConfiguration.PROXY_USER());
    }

    @Override
    public String getJobName() {
        return getJobProps().get(FlowExecutionEntranceConfiguration.JOB_ID());
    }


}
