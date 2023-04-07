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

package com.webank.wedatasphere.dss.common.protocol;

public enum JobStatus {

    /**
     * JobStatus是用来进行发布任务的状态的追踪
     */

    Inited("Inited", 1),
    Running("Running", 2),
    Success("Success", 3),
    Failed("Failed", 4);

    private String status;
    private int index;

    JobStatus(String status, int index){
        this.status = status;
        this.index = index;
    }

    public static JobStatus getJobStatusByIndex(Integer index) {
        if (JobStatus.Inited.getIndex() == index) {
            return JobStatus.Inited;
        } else if (JobStatus.Running.getIndex() == index) {
            return JobStatus.Running;
        } else if (JobStatus.Success.getIndex() == index) {
            return JobStatus.Success;
        } else {
            return JobStatus.Failed;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
