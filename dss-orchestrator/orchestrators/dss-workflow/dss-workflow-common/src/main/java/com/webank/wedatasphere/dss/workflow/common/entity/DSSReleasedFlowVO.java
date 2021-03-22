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

package com.webank.wedatasphere.dss.workflow.common.entity;

import java.util.Date;
import java.util.List;

/**
 * created by cooperyang on 2020/3/22
 * Description:
 */
public class DSSReleasedFlowVO {
    private Integer id;
    private String workflowName;
    private String projectName;
    private String scheduleTime;
    private String latestVersion;
    private String lastScheduleStatus;
    private String committer;
    private String lastUpdater;
    private Date lastUpdateTime;
    private List<String> accessUsers;
    private Integer privModel;
    private ScheduleInfo scheduleInfo;
    private Long projectVersionId;
    private Long projectId;
    private FlowPriv flowPriv;

    public static class FlowPriv{
        private Integer privModel;
        private List<String> usernames;

        public Integer getPrivModel() {
            return privModel;
        }

        public void setPrivModel(Integer privModel) {
            this.privModel = privModel;
        }

        public List<String> getUsernames() {
            return usernames;
        }

        public void setUsernames(List<String> usernames) {
            this.usernames = usernames;
        }
    }

    public static class ScheduleInfo{
        private String scheduleTime;
        private String alarmUserEmails;
        private String alarmLevel;

        public String getScheduleTime() {
            return scheduleTime;
        }

        public void setScheduleTime(String scheduleTime) {
            this.scheduleTime = scheduleTime;
        }

        public String getAlarmUserEmails() {
            return alarmUserEmails;
        }

        public void setAlarmUserEmails(String alarmUserEmails) {
            this.alarmUserEmails = alarmUserEmails;
        }

        public String getAlarmLevel() {
            return alarmLevel;
        }

        public void setAlarmLevel(String alarmLevel) {
            this.alarmLevel = alarmLevel;
        }
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public String getLastScheduleStatus() {
        return lastScheduleStatus;
    }

    public void setLastScheduleStatus(String lastScheduleStatus) {
        this.lastScheduleStatus = lastScheduleStatus;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public String getLastUpdater() {
        return lastUpdater;
    }

    public void setLastUpdater(String lastUpdater) {
        this.lastUpdater = lastUpdater;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<String> getAccessUsers() {
        return accessUsers;
    }

    public void setAccessUsers(List<String> accessUsers) {
        this.accessUsers = accessUsers;
    }

    public Integer getPrivModel() {
        return privModel;
    }

    public void setPrivModel(Integer privModel) {
        this.privModel = privModel;
    }

    public ScheduleInfo getScheduleInfo() {
        return scheduleInfo;
    }

    public void setScheduleInfo(ScheduleInfo scheduleInfo) {
        this.scheduleInfo = scheduleInfo;
    }

    public Long getProjectVersionId() {
        return projectVersionId;
    }

    public void setProjectVersionId(Long projectVersionId) {
        this.projectVersionId = projectVersionId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public FlowPriv getFlowPriv() {
        return flowPriv;
    }

    public void setFlowPriv(FlowPriv flowPriv) {
        this.flowPriv = flowPriv;
    }
}
