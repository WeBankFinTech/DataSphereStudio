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

package com.webank.wedatasphere.dss.orchestrator.publish.job;

import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import java.util.Date;
import java.util.List;


public class ConversionJobEntity {

    private DSSProject project;

    private List<Long> orcIdList;

    private List<Long> refAppIdList;

    private Workspace workspace;

    private String userName;

    private List<DSSLabel> labels;

    private Date createTime;

    private Date updateTime;

    private ResponseOperateOrchestrator response;

    public DSSProject getProject() {
        return project;
    }

    public void setProject(DSSProject project) {
        this.project = project;
    }

    public List<Long> getOrcIdList() {
        return orcIdList;
    }

    public void setOrcIdList(List<Long> orcIdList) {
        this.orcIdList = orcIdList;
    }

    public List<Long> getRefAppIdList() {
        return refAppIdList;
    }

    public void setRefAppIdList(List<Long> refAppIdList) {
        this.refAppIdList = refAppIdList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public ResponseOperateOrchestrator getResponse() {
        return response;
    }

    public void setResponse(ResponseOperateOrchestrator response) {
        updateTime = new Date();
        this.response = response;
    }

    public List<DSSLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<DSSLabel> labels) {
        this.labels = labels;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
