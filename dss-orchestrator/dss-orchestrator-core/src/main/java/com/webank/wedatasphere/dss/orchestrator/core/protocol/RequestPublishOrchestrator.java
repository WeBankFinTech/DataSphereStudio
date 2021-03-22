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

package com.webank.wedatasphere.dss.orchestrator.core.protocol;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.entity.PublishType;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;

/**
 * created by cooperyang on 2021/1/5
 * Description:
 */
public class RequestPublishOrchestrator {

    private String userName;
    private String projectName;
    private Long projectId;
    private List<Long> orcIds;
    private PublishType publishType;
    private Workspace workspace;
    private List<DSSLabel> dssLabels;


    public RequestPublishOrchestrator(String userName,
                                      String projectName, Long projectId, List<Long> orcIds,
                                      PublishType publishType, Workspace workspace, List<DSSLabel> dssLabels) {
        this.userName = userName;
        this.projectName = projectName;
        this.projectId = projectId;
        this.orcIds = orcIds;
        this.publishType = publishType;
        this.workspace = workspace;
        this.dssLabels = dssLabels;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<Long> getOrcIds() {
        return orcIds;
    }

    public void setOrcIds(List<Long> orcIds) {
        this.orcIds = orcIds;
    }

    public PublishType getPublishType() {
        return publishType;
    }

    public void setPublishType(PublishType publishType) {
        this.publishType = publishType;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public List<DSSLabel> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }
}
