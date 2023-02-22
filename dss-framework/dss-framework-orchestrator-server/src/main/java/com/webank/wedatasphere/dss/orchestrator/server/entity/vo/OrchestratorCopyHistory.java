package com.webank.wedatasphere.dss.orchestrator.server.entity.vo;


public class OrchestratorCopyHistory {

    private String id;

    private String userName;

    private String workspaceName;

    private String sourceOrchestratorName;

    private String targetOrchestratorName;

    private String sourceProjectName;

    private String targetProjectName;

    /**
     * 目标工作流节点后缀
     */
    private String workflowNodeSuffix;

    private String microserverName;

    private String exceptionInfo;

    /**
     * 复制任务最终状态，1成功，0失败
     */
    private Integer status;

    /**
     * 当前编排是否在被复制，1有，0没有
     */
    private Integer isCopying;

    /**
     * 复制开始时间
     */
    private String startTime;

    /**
     * 复制结束时间
     */
    private String endTime;

    public OrchestratorCopyHistory() {
    }

    public OrchestratorCopyHistory(String id, String username, String workspaceName, String sourceOrchestratorName, String targetOrchestratorName, String sourceProjectName, String targetProjectName, String workflowNodeSuffix, String microserverName, String exceptionInfo, Integer status, Integer isCopying, String startTime, String endTime) {
        this.id = id;
        this.userName = username;
        this.workspaceName = workspaceName;
        this.sourceOrchestratorName = sourceOrchestratorName;
        this.targetOrchestratorName = targetOrchestratorName;
        this.sourceProjectName = sourceProjectName;
        this.targetProjectName = targetProjectName;
        this.workflowNodeSuffix = workflowNodeSuffix;
        this.microserverName = microserverName;
        this.exceptionInfo = exceptionInfo;
        this.status = status;
        this.isCopying = isCopying;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() { return userName; }

    public void setUsername(String username) { this.userName = username; }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getSourceOrchestratorName() {
        return sourceOrchestratorName;
    }

    public void setSourceOrchestratorName(String sourceOrchestratorName) {
        this.sourceOrchestratorName = sourceOrchestratorName;
    }

    public String getTargetOrchestratorName() {
        return targetOrchestratorName;
    }

    public void setTargetOrchestratorName(String targetOrchestratorName) {
        this.targetOrchestratorName = targetOrchestratorName;
    }

    public String getSourceProjectName() {
        return sourceProjectName;
    }

    public void setSourceProjectName(String sourceProjectName) {
        this.sourceProjectName = sourceProjectName;
    }

    public String getTargetProjectName() {
        return targetProjectName;
    }

    public void setTargetProjectName(String targetProjectName) {
        this.targetProjectName = targetProjectName;
    }

    public String getWorkflowNodeSuffix() {
        return workflowNodeSuffix;
    }

    public void setWorkflowNodeSuffix(String workflowNodeSuffix) {
        this.workflowNodeSuffix = workflowNodeSuffix;
    }

    public String getMicroserverName() {
        return microserverName;
    }

    public void setMicroserverName(String microserverName) {
        this.microserverName = microserverName;
    }

    public String getExceptionInfo() {
        return exceptionInfo;
    }

    public void setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsCopying() {
        return isCopying;
    }

    public void setIsCopying(Integer isCopying) {
        this.isCopying = isCopying;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "OrchestratorCopyHistory{" +
                "id=" + id +
                "username=" + userName +
                ", workspaceName='" + workspaceName + '\'' +
                ", sourceOrchestratorName='" + sourceOrchestratorName + '\'' +
                ", targetOrchestratorName='" + targetOrchestratorName + '\'' +
                ", sourceProjectName='" + sourceProjectName + '\'' +
                ", targetProjectName='" + targetProjectName + '\'' +
                ", workflowNodeSuffix='" + workflowNodeSuffix + '\'' +
                ", microserverName='" + microserverName + '\'' +
                ", exceptionInfo='" + exceptionInfo + '\'' +
                ", status=" + status +
                ", isCopying=" + isCopying +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
