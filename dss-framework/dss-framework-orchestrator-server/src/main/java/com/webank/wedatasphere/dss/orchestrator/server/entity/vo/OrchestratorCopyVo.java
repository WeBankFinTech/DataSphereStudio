package com.webank.wedatasphere.dss.orchestrator.server.entity.vo;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.io.Serializable;

public class OrchestratorCopyVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private Long sourceProjectId;
    private String sourceProjectName;
    private Long targetProjectId;
    private String targetProjectName;
    private DSSOrchestratorInfo orchestrator;
    private String targetOrchestratorName;
    private String workflowNodeSuffix;
    private DSSLabel dssLabel;
    private Workspace workspace;
    private Long copyTaskId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getSourceProjectId() {
        return sourceProjectId;
    }

    public void setSourceProjectId(Long sourceProjectId) {
        this.sourceProjectId = sourceProjectId;
    }

    public String getSourceProjectName() {
        return sourceProjectName;
    }

    public void setSourceProjectName(String sourceProjectName) {
        this.sourceProjectName = sourceProjectName;
    }

    public Long getTargetProjectId() {
        return targetProjectId;
    }

    public void setTargetProjectId(Long targetProjectId) {
        this.targetProjectId = targetProjectId;
    }

    public String getTargetProjectName() {
        return targetProjectName;
    }

    public void setTargetProjectName(String targetProjectName) {
        this.targetProjectName = targetProjectName;
    }

    public DSSOrchestratorInfo getOrchestrator() {
        return orchestrator;
    }

    public void setOrchestrator(DSSOrchestratorInfo orchestrator) {
        this.orchestrator = orchestrator;
    }

    public String getTargetOrchestratorName() {
        return targetOrchestratorName;
    }

    public void setTargetOrchestratorName(String targetOrchestratorName) {
        this.targetOrchestratorName = targetOrchestratorName;
    }

    public String getWorkflowNodeSuffix() {
        return workflowNodeSuffix;
    }

    public void setWorkflowNodeSuffix(String workflowNodeSuffix) {
        this.workflowNodeSuffix = workflowNodeSuffix;
    }

    public DSSLabel getDssLabel() {
        return dssLabel;
    }

    public void setDssLabel(DSSLabel dssLabel) {
        this.dssLabel = dssLabel;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public Long getCopyTaskId() {
        return copyTaskId;
    }

    public void setCopyTaskId(Long copyTaskId) {
        this.copyTaskId = copyTaskId;
    }
}

