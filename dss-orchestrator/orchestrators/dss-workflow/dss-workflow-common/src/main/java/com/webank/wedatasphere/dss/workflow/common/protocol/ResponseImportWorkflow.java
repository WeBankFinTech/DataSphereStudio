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

package com.webank.wedatasphere.dss.workflow.common.protocol;

import com.webank.wedatasphere.dss.common.protocol.JobStatus;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;

import java.util.List;



public class ResponseImportWorkflow {


    private JobStatus status;
    /**
     * the key of this map is workflow id, and value is json content.
      */
    private List<DSSFlow> workflows;

    public ResponseImportWorkflow(JobStatus status, List<DSSFlow> workflows) {
        this.status = status;
        this.workflows = workflows;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public List<DSSFlow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<DSSFlow> workflows) {
        this.workflows = workflows;
    }
}
