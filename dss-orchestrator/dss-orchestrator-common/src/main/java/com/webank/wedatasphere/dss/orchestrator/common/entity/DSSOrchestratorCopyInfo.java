package com.webank.wedatasphere.dss.orchestrator.common.entity;

import java.util.Date;
import java.util.List;

public class DSSOrchestratorCopyInfo {

    private String id;

    private String username;

    private String type;

    private Long workspaceId;

    private Long sourceOrchestratorId;

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
     * 复制任务最终状态,0失败，1成功
     */
    private Integer status;

    private String instanceName;

    /**
     * 当前编排是否在被复制，1有，0没有
     */
    private Integer isCopying;

    /**
     * 复制成功的节点
     */
    private List<String> successNode;

    /**
     * 复制开始时间
     */
    private Date startTime;

    /**
     * 复制结束时间
     */
    private Date endTime;

    public DSSOrchestratorCopyInfo() {
    }

    public DSSOrchestratorCopyInfo(String id) {
            this.id = id;
    }

    public DSSOrchestratorCopyInfo(String id, String username, String type, Long workspaceId, Long sourceOrchestratorId,
                                   String sourceOrchestratorName, String targetOrchestratorName,
                                   String sourceProjectName, String targetProjectName, String workflowNodeSuffix,
                                   String microserverName, Integer isCopying, Date startTime, Date endTime, String instanceName) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.workspaceId = workspaceId;
        this.sourceOrchestratorId = sourceOrchestratorId;
        this.sourceOrchestratorName = sourceOrchestratorName;
        this.targetOrchestratorName = targetOrchestratorName;
        this.sourceProjectName = sourceProjectName;
        this.targetProjectName = targetProjectName;
        this.workflowNodeSuffix = workflowNodeSuffix;
        this.microserverName = microserverName;
        this.isCopying = isCopying;
        this.startTime = startTime;
        this.endTime = endTime;
        this.instanceName = instanceName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getSourceOrchestratorId() {
        return sourceOrchestratorId;
    }

    public void setSourceOrchestratorId(Long sourceOrchestratorId) {
        this.sourceOrchestratorId = sourceOrchestratorId;
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

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Integer getIsCopying() {
        return isCopying;
    }

    public void setIsCopying(Integer isCopying) {
        this.isCopying = isCopying;
    }

    public String getSuccessNode() {
        return successNode == null ? null : String.join(",", successNode);
    }

    public void setSuccessNode(List<String> successNode) {
        this.successNode = successNode;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "DSSOrchestratorCopyInfo{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", workspaceId=" + workspaceId +
                ", sourceOrchestratorId=" + sourceOrchestratorId +
                ", sourceOrchestratorName='" + sourceOrchestratorName + '\'' +
                ", targetOrchestratorName='" + targetOrchestratorName + '\'' +
                ", sourceProjectName='" + sourceProjectName + '\'' +
                ", targetProjectName='" + targetProjectName + '\'' +
                ", workflowNodeSuffix='" + workflowNodeSuffix + '\'' +
                ", microserverName='" + microserverName + '\'' +
                ", exceptionInfo='" + exceptionInfo + '\'' +
                ", status='" + status + '\'' +
                ", instanceName='" + instanceName + '\'' +
                ", isCopying=" + isCopying +
                ", successNode=" + successNode +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
