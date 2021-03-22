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

package com.webank.wedatasphere.dss.orchestrator.common.entity;



import com.webank.wedatasphere.dss.workflow.common.entity.DSSReleasedFlowVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * created by cooperyang on 2021/1/22
 * Description:
 */
public class OrchestratorProdDetail {

    private String orchestratorName;

    private Long orchestratorId;

    private Long orchestratorVersionId;

    private String scheduleTime;

    private String latestVersion;

    private String releaseUser;

    private String lastUpdater;

    private String lastUpdateTime;

    private String status;

    private String comment;

    private List<String> labels;

    private String scheduleSettings;

    private Integer privModel;
    private String projectName;
    private DSSReleasedFlowVO.ScheduleInfo scheduleInfo;
    private Long projectId;
    private DSSReleasedFlowVO.FlowPriv flowPriv;


    public OrchestratorProdDetail(){
        this.projectName = "test20200126";
        DSSReleasedFlowVO.ScheduleInfo scheduleInfo = new DSSReleasedFlowVO.ScheduleInfo();
        scheduleInfo.setAlarmLevel("INFO");
        scheduleInfo.setAlarmUserEmails("jianfuzhang, cooperyang");
        scheduleInfo.setScheduleTime("0 /5 * * * ? *");
        this.scheduleInfo = scheduleInfo;
        this.privModel = 2;
        this.projectId = 10L;
        DSSReleasedFlowVO.FlowPriv flowPriv = new DSSReleasedFlowVO.FlowPriv();
        flowPriv.setPrivModel(2);
        flowPriv.setUsernames(new ArrayList<>(Arrays.asList("cooperyang", "jianfuzhang", "johnnwang")));
        this.flowPriv = flowPriv;
    }



    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public Long getOrchestratorVersionId() {
        return orchestratorVersionId;
    }

    public void setOrchestratorVersionId(Long orchestratorVersionId) {
        this.orchestratorVersionId = orchestratorVersionId;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }

    public String getLastUpdater() {
        return lastUpdater;
    }

    public void setLastUpdater(String lastUpdater) {
        this.lastUpdater = lastUpdater;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getScheduleSettings() {
        return scheduleSettings;
    }

    public void setScheduleSettings(String scheduleSettings) {
        this.scheduleSettings = scheduleSettings;
    }

    public Integer getPrivModel() {
        return privModel;
    }

    public void setPrivModel(Integer privModel) {
        this.privModel = privModel;
    }

    public DSSReleasedFlowVO.ScheduleInfo getScheduleInfo() {
        return scheduleInfo;
    }

    public void setScheduleInfo(DSSReleasedFlowVO.ScheduleInfo scheduleInfo) {
        this.scheduleInfo = scheduleInfo;
    }


    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public DSSReleasedFlowVO.FlowPriv getFlowPriv() {
        return flowPriv;
    }

    public void setFlowPriv(DSSReleasedFlowVO.FlowPriv flowPriv) {
        this.flowPriv = flowPriv;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return "OrchestratorProdDetail{" +
                "orchestratorName='" + orchestratorName + '\'' +
                ", orchestratorId=" + orchestratorId +
                ", orchestratorVersionId=" + orchestratorVersionId +
                ", scheduleTime='" + scheduleTime + '\'' +
                ", latestVersion='" + latestVersion + '\'' +
                ", releaseUser='" + releaseUser + '\'' +
                ", lastUpdater='" + lastUpdater + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                '}';
    }
}
