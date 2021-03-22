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

package com.webank.wedatasphere.dss.workflow.common.protocol;

import com.webank.wedatasphere.dss.common.protocol.JobStatus;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/12/29 19:53
 */
public class ResponseImportWorkflow {


    private JobStatus status;

    private List<Long> workflowIds;

    public ResponseImportWorkflow(JobStatus status, List<Long> workflowIds) {
        this.status = status;
        this.workflowIds = workflowIds;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public List<Long> getWorkflowIds() {
        return workflowIds;
    }

    public void setWorkflowIds(List<Long> workflowIds) {
        this.workflowIds = workflowIds;
    }
}
