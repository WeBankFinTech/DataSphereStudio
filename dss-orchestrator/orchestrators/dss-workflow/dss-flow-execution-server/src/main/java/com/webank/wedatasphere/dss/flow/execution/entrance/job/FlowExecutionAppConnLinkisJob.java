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

package com.webank.wedatasphere.dss.flow.execution.entrance.job;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.job.AbstractAppConnLinkisJob;
import org.apache.commons.lang3.StringUtils;


public class FlowExecutionAppConnLinkisJob extends AbstractAppConnLinkisJob {


    @Override
    public String getSubmitUser() {
        return getJobProps().get(FlowExecutionEntranceConfiguration.FLOW_SUBMIT_USER());
    }

    @Override
    public String getUser() {
        String labels = getJobProps().getOrDefault(DSSCommonUtils.DSS_LABELS_KEY, DSSCommonUtils.ENV_LABEL_VALUE_DEV);
        String submitUser = getSubmitUser();
        if(DSSCommonUtils.ENV_LABEL_VALUE_DEV.equals(labels)) {
            return submitUser;
        } else {
            String proxyUser = getJobProps().get(FlowExecutionEntranceConfiguration.PROXY_USER());
            if(StringUtils.isNotEmpty(proxyUser)){
                return proxyUser;
            }else {
                return submitUser;
            }
        }
    }

    @Override
    public String getJobName() {
        return getJobProps().get(FlowExecutionEntranceConfiguration.JOB_ID());
    }


}
