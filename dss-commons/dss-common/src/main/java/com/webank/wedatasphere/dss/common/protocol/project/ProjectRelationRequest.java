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

package com.webank.wedatasphere.dss.common.protocol.project;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;

/**
 * created by cooperyang on 2021/1/18
 * Description:
 */
public class ProjectRelationRequest {

    private Long dssProjectId;

    private String appConnName;

    private List<DSSLabel> dssLabels;

    public ProjectRelationRequest(Long dssProjectId, String appconnName, List<DSSLabel> dssLabels) {
        this.dssProjectId = dssProjectId;
        this.appConnName = appconnName;
        this.dssLabels = dssLabels;
    }

    public Long getDssProjectId() {
        return dssProjectId;
    }

    public void setDssProjectId(Long dssProjectId) {
        this.dssProjectId = dssProjectId;
    }

    public String getAppconnName() {
        return appConnName;
    }

    public void setAppconnName(String appconnName) {
        this.appConnName = appconnName;
    }

    public List<DSSLabel> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }
}
