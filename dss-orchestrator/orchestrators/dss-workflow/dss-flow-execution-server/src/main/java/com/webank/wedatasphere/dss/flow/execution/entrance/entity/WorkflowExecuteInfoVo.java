package com.webank.wedatasphere.dss.flow.execution.entrance.entity;

import java.util.List;
import java.util.Map;

public class WorkflowExecuteInfoVo extends WorkflowExecuteInfo {
    List<Map<String, Object>> runningJobsList;
    List<Map<String, Object>> failedJobsList;
    List<Map<String, Object>> succeedJobsList;
    List<Map<String, Object>> pendingJobsList;
    List<Map<String, Object>> skippedJobsList;

    public WorkflowExecuteInfoVo() {
    }

    public List<Map<String, Object>> getRunningJobsList() {
        return runningJobsList;
    }

    public void setRunningJobsList(List<Map<String, Object>> runningJobsList) {
        this.runningJobsList = runningJobsList;
    }

    public List<Map<String, Object>> getFailedJobsList() {
        return failedJobsList;
    }

    public void setFailedJobsList(List<Map<String, Object>> failedJobsList) {
        this.failedJobsList = failedJobsList;
    }

    public List<Map<String, Object>> getSucceedJobsList() {
        return succeedJobsList;
    }

    public void setSucceedJobsList(List<Map<String, Object>> succeedJobsList) {
        this.succeedJobsList = succeedJobsList;
    }

    public List<Map<String, Object>> getPendingJobsList() {
        return pendingJobsList;
    }

    public void setPendingJobsList(List<Map<String, Object>> pendingJobsList) {
        this.pendingJobsList = pendingJobsList;
    }

    public List<Map<String, Object>> getSkippedJobsList() {
        return skippedJobsList;
    }

    public void setSkippedJobsList(List<Map<String, Object>> skippedJobsList) {
        this.skippedJobsList = skippedJobsList;
    }


}
