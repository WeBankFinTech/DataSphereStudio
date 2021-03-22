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

package com.webank.wedatasphere.dss.workflow.scheduler;


import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;

import java.util.Date;

public class DssJobInfo {

    private Long id;
    private DssJobStatus status;
    private Date createTime;
    private Date updateTime;
    private String msg;

    public Boolean timeout() {
        return System.currentTimeMillis() - updateTime.getTime() > (int) DSSWorkFlowConstant.CACHE_TIMEOUT.getValue();
    }

    public Boolean isExecute() {
        return status != null && (status == DssJobStatus.Inited || status == DssJobStatus.Running);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DssJobStatus getStatus() {
        return status;
    }

    public void setStatus(DssJobStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
