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

package com.webank.wedatasphere.dss.orchestrator.common.protocol;

import com.webank.wedatasphere.dss.common.protocol.JobStatus;


public class ResponseOperateOrchestrator {

    private JobStatus jobStatus;
    private String message;

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public ResponseOperateOrchestrator setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseOperateOrchestrator setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isCompleted() {
        return isSucceed() || isFailed();
    }

    public boolean isSucceed() {
        return jobStatus == JobStatus.Success;
    }

    public boolean isFailed() {
        return jobStatus == JobStatus.Failed;
    }

    public boolean isRunning() {
        return jobStatus == JobStatus.Running;
    }

    public static ResponseOperateOrchestrator success() {
        ResponseOperateOrchestrator response = new ResponseOperateOrchestrator();
        response.setJobStatus(JobStatus.Success);
        return response;
    }

    public static ResponseOperateOrchestrator success(String message) {
        ResponseOperateOrchestrator response = new ResponseOperateOrchestrator();
        response.setJobStatus(JobStatus.Success);
        response.setMessage(message);
        return response;
    }

    public static ResponseOperateOrchestrator failed(String message) {
        ResponseOperateOrchestrator response = new ResponseOperateOrchestrator();
        response.setJobStatus(JobStatus.Failed);
        response.setMessage(message);
        return response;
    }

    public static ResponseOperateOrchestrator running() {
        ResponseOperateOrchestrator response = new ResponseOperateOrchestrator();
        response.setJobStatus(JobStatus.Running);
        return response;
    }

    public static ResponseOperateOrchestrator inited() {
        ResponseOperateOrchestrator response = new ResponseOperateOrchestrator();
        response.setJobStatus(JobStatus.Inited);
        return response;
    }
}
