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

package com.webank.wedatasphere.dss.common.entity.project;

import java.util.Date;

/**
 * Created by enjoyyin on 2019/9/18.
 */
public class DSSProjectVersion implements ProjectVersion {

    private Long id;
    private Long projectID;
    private String version;
    private String comment;
    private Date updateTime;
    private Long updatorID;
    private Integer lock;
    private String updator;
    private Boolean isNotPublish;
    private DSSProjectPublishHistory publishHistory;

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setVersion() {
        this.version = version;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectID() {
        return projectID;
    }

    public void setProjectID(Long projectID) {
        this.projectID = projectID;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdatorID() {
        return updatorID;
    }

    public void setUpdatorID(Long updatorID) {
        this.updatorID = updatorID;
    }

    public Integer getLock() {
        return lock;
    }

    public void setLock(Integer lock) {
        this.lock = lock;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public Boolean getNotPublish() {
        return isNotPublish;
    }

    public void setNotPublish(Boolean notPublish) {
        isNotPublish = notPublish;
    }

    public DSSProjectPublishHistory getPublishHistory() {
        return publishHistory;
    }

    public void setPublishHistory(DSSProjectPublishHistory publishHistory) {
        this.publishHistory = publishHistory;
    }
}
