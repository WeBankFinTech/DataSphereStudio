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

package com.webank.wedatasphere.dss.server.publish;


import com.webank.wedatasphere.dss.server.conf.DSSServerConf;

import java.util.Date;


public class PublishProjectCache {

    private Long projectVersionID;
    private PublishJobEnum status;
    private Date createTime;
    private Date updateTime;
    private String msg;

    public Boolean timeout(){
        return  System.currentTimeMillis() - updateTime.getTime() > (int) DSSServerConf.CACHE_TIMEOUT.getValue();
    }

    public Boolean isPublishing() {
        return status != null && (status == PublishJobEnum.Inited || status == PublishJobEnum.Running);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public PublishJobEnum getStatus() {
        return status;
    }

    public void setStatus(PublishJobEnum status) {
        this.status = status;
    }

    public Long getProjectVersionID() {
        return projectVersionID;
    }

    public void setProjectVersionID(Long projectVersionID) {
        this.projectVersionID = projectVersionID;
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
