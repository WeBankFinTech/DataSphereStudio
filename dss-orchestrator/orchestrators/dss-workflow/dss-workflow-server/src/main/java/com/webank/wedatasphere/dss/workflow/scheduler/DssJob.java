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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DssJob implements Runnable {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected String user;
    protected Long id;
    protected DssJobListener dssJobListener;
    protected DssJobStatus status;

    public DssJobListener getDssJobListener() {
        return dssJobListener;
    }

    public void setDssJobListener(DssJobListener dssJobListener) {
        this.dssJobListener = dssJobListener;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public DssJobStatus getStatus() {
        return status;
    }

    public void setStatus(DssJobStatus status) {
        this.status = status;
    }
}
