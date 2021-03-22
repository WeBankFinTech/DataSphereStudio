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

package com.webank.wedatasphere.dss.framework.release.entity.task;

/**
 * created by cooperyang on 2021/2/22
 * Description:
 */
public class PublishStatus {
    private String status;
    private String errorMsg;
    private Long releaseTaskId;


    public PublishStatus(String status, Long releaseTaskId){
        this(status, "", releaseTaskId);
    }

    public PublishStatus(String status, String errorMsg, Long releaseTaskId){
        this.status = status;
        this.errorMsg = errorMsg;
        this.releaseTaskId = releaseTaskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Long getReleaseTaskId() {
        return releaseTaskId;
    }

    public void setReleaseTaskId(Long releaseTaskId) {
        this.releaseTaskId = releaseTaskId;
    }
}
