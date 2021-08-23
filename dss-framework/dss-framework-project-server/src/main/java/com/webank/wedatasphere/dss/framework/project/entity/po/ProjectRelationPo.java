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

package com.webank.wedatasphere.dss.framework.project.entity.po;


public class ProjectRelationPo {

    private Long dssProjectId;

    private Long appInstanceId;

    private Long appInstanceProjectId;


    public ProjectRelationPo(Long dssProjectId, Long appInstanceId, Long appInstanceProjectId) {
        this.dssProjectId = dssProjectId;
        this.appInstanceId = appInstanceId;
        this.appInstanceProjectId = appInstanceProjectId;
    }

    public Long getDssProjectId() {
        return dssProjectId;
    }

    public void setDssProjectId(Long dssProjectId) {
        this.dssProjectId = dssProjectId;
    }

    public Long getAppInstanceId() {
        return appInstanceId;
    }

    public void setAppInstanceId(Long appInstanceId) {
        this.appInstanceId = appInstanceId;
    }

    public Long getAppInstanceProjectId() {
        return appInstanceProjectId;
    }

    public void setAppInstanceProjectId(Long appInstanceProjectId) {
        this.appInstanceProjectId = appInstanceProjectId;
    }
}
