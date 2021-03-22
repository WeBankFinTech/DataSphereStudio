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

package com.webank.wedatasphere.dss.apiservice.core.bo;

import com.webank.wedatasphere.linkis.ujes.client.response.JobExecuteResult;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/09/03 07:28 PM
 */
public class ApiServiceJob {

    private String submitUser;
    private String proxyUser;
    private JobExecuteResult jobExecuteResult;
    public String getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(String submitUser) {
        this.submitUser = submitUser;
    }



    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }
    public JobExecuteResult getJobExecuteResult() {
        return jobExecuteResult;
    }

    public void setJobExecuteResult(JobExecuteResult jobExecuteResult) {
        this.jobExecuteResult = jobExecuteResult;
    }
}
