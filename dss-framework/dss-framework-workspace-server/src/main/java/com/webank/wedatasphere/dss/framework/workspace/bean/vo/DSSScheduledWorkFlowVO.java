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

package com.webank.wedatasphere.dss.framework.workspace.bean.vo;

import java.util.Date;

/**
 * created by cooperyang on 2020/3/8
 * Description:
 */
public class DSSScheduledWorkFlowVO extends AbstractDSSVO{
    private String workflowName;
    private String project;
    private Date scheduledTime;
    private String newestVersion;
    private String lastScheduledStatus;
    private String committer;
    private String lastOperator;
    private Date lastOperateTime;
    private String workflowJson;
    private int workflowId;
    public DSSScheduledWorkFlowVO() {
    }

    public DSSScheduledWorkFlowVO(String workflowName, String project, Date scheduledTime, String newestVersion,
                                  String lastScheduledStatus, String committer, String lastOperator, Date lastOperateTime) {
        this.workflowName = workflowName;
        this.project = project;
        this.scheduledTime = scheduledTime;
        this.newestVersion = newestVersion;
        this.lastScheduledStatus = lastScheduledStatus;
        this.committer = committer;
        this.lastOperator = lastOperator;
        this.lastOperateTime = lastOperateTime;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getNewestVersion() {
        return newestVersion;
    }

    public void setNewestVersion(String newestVersion) {
        this.newestVersion = newestVersion;
    }

    public String getLastScheduledStatus() {
        return lastScheduledStatus;
    }

    public void setLastScheduledStatus(String lastScheduledStatus) {
        this.lastScheduledStatus = lastScheduledStatus;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public String getLastOperator() {
        return lastOperator;
    }

    public void setLastOperator(String lastOperator) {
        this.lastOperator = lastOperator;
    }

    public Date getLastOperateTime() {
        return lastOperateTime;
    }

    public void setLastOperateTime(Date lastOperateTime) {
        this.lastOperateTime = lastOperateTime;
    }

    public String getWorkflowJson() {
        return workflowJson;
    }

    public void setWorkflowJson(String workflowJson) {
        this.workflowJson = workflowJson;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public static void main(String[] args){
//        DSSScheduledWorkFlowVO vo1 =
//                new DSSScheduledWorkFlowVO("workflowName", "projectName",
//                        "每天三点钟", "v0001", "成功", "enjoyyin", "cooperyang", "2020-03-08 14:24:01");
//        DSSScheduledWorkFlowVO vo2 =
//                new DSSScheduledWorkFlowVO("workflowName1", "projectName1",
//                        "每天三点钟", "v0002", "失败", "enjoyyin", "chaogefeng", "2020-03-08 14:30:01");
//
//        vo1.setWorkflowJson("工作流json");
//        vo1.setWorkflowId(456);
//        vo2.setWorkflowJson("工作流json");
//        vo2.setWorkflowId(789);
//        List<DSSScheduledWorkFlowVO> list = Arrays.asList(vo1, vo2);
//        System.out.println(VOUtils.gson.toJson(list));

    }


}
