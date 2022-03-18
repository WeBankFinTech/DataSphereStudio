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
import java.util.Map;


public class ConversionJobEntity {

    private DSSProject project;

    //这里的 key 为 DSS 具体编排（如 DSS 工作流）的 id；这里的 value 为 DSS 编排所对应的第三方调度系统的工作流 ID
    //请注意：由于对接的 SchedulerAppConn 调度系统有可能没有实现 OrchestrationService，
    //所以可能存在 DSS 在创建 DSS 编排时，无法同步去 SchedulerAppConn 创建工作流的情况，从而导致这个 Map 的所有 value 都为 null。
    private Map<Long, Long> orchestrationIdMap;

    // DSS编排的ID列表
    private List<Long> orcIdList;

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

    public Map<Long, Long> getOrchestrationIdMap() {
        return orchestrationIdMap;
    }

    public void setOrchestrationIdMap(Map<Long, Long> orchestrationIdMap) {
        this.orchestrationIdMap = orchestrationIdMap;
    }

    public List<Long> getOrcIdList() {
        return orcIdList;
    }

    public void setOrcIdList(List<Long> orcIdList) {
        this.orcIdList = orcIdList;
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
