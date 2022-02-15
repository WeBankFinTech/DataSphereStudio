package com.webank.wedatasphere.dss.flow.execution.entrance.entity;

import java.util.Date;

public class WorkflowExecuteInfo {
    private Long id;
    private Long taskId;
    private Long flowId;
    private Integer status;
    private String version;
    private String failedJobs;
    private String PendingJobs;
    private String skippedJobs;
    private String succeedJobs;
    private String runningJobs;
    private Date createtime;
    private Date updatetime;

    public WorkflowExecuteInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFailedJobs() {
        return failedJobs;
    }

    public void setFailedJobs(String failedJobs) {
        this.failedJobs = failedJobs;
    }

    public String getPendingJobs() {
        return PendingJobs;
    }

    public void setPendingJobs(String pendingJobs) {
        PendingJobs = pendingJobs;
    }

    public String getSkippedJobs() {
        return skippedJobs;
    }

    public void setSkippedJobs(String skippedJobs) {
        this.skippedJobs = skippedJobs;
    }

    public String getSucceedJobs() {
        return succeedJobs;
    }

    public void setSucceedJobs(String succeedJobs) {
        this.succeedJobs = succeedJobs;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getRunningJobs() {
        return runningJobs;
    }

    public void setRunningJobs(String runningJobs) {
        this.runningJobs = runningJobs;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}
