package com.webank.wedatasphere.dss.orchestrator.server.entity.vo;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.io.Serializable;

public class OrchestratorCopyVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String username;
    private final Long sourceProjectId;
    private final String sourceProjectName;
    private final Long targetProjectId;
    private final String targetProjectName;
    private final DSSOrchestratorInfo orchestrator;
    private final String targetOrchestratorName;
    private final String workflowNodeSuffix;
    private final DSSLabel dssLabel;
    private final Workspace workspace;
    private final Long copyTaskId;
    private final String instanceName;

    private OrchestratorCopyVo(Builder builder) {
        this.username = builder.username;
        this.sourceProjectId = builder.sourceProjectId;
        this.sourceProjectName = builder.sourceProjectName;
        this.targetProjectId = builder.targetProjectId;
        this.targetProjectName = builder.targetProjectName;
        this.orchestrator = builder.orchestrator;
        this.targetOrchestratorName = builder.targetOrchestratorName;
        this.workflowNodeSuffix = builder.workflowNodeSuffix;
        this.dssLabel = builder.dssLabel;
        this.workspace = builder.workspace;
        this.copyTaskId = builder.copyTaskId;
        this.instanceName = builder.instanceName;
    }


    public static class Builder {

        private final String username;
        private final Long sourceProjectId;
        private final String sourceProjectName;
        private final Long targetProjectId;
        private final String targetProjectName;
        private final DSSOrchestratorInfo orchestrator;
        private final String targetOrchestratorName;
        private final String workflowNodeSuffix;
        private final DSSLabel dssLabel;
        private final Workspace workspace;
        private Long copyTaskId;
        private final String instanceName;

        public Builder(String username, Long sourceProjectId, String sourceProjectName, Long targetProjectId,
                       String targetProjectName, DSSOrchestratorInfo orchestrator, String targetOrchestratorName,
                       String workflowNodeSuffix, DSSLabel dssLabel, Workspace workspace, String instanceName) {
            this.username = username;
            this.sourceProjectId = sourceProjectId;
            this.sourceProjectName = sourceProjectName;
            this.targetProjectId = targetProjectId;
            this.targetProjectName = targetProjectName;
            this.orchestrator = orchestrator;
            this.targetOrchestratorName = targetOrchestratorName;
            this.workflowNodeSuffix = workflowNodeSuffix;
            this.dssLabel = dssLabel;
            this.workspace = workspace;
            this.instanceName = instanceName;
        }

        public Builder setCopyTaskId(Long copyTaskId){
            this.copyTaskId = copyTaskId;
            return this;
        }

        public OrchestratorCopyVo build(){
            return new OrchestratorCopyVo(this);
        }
    }

    public String getUsername() {
        return username;
    }

    public Long getSourceProjectId() {
        return sourceProjectId;
    }

    public String getSourceProjectName() {
        return sourceProjectName;
    }

    public Long getTargetProjectId() {
        return targetProjectId;
    }

    public String getTargetProjectName() {
        return targetProjectName;
    }

    public DSSOrchestratorInfo getOrchestrator() {
        return orchestrator;
    }

    public String getTargetOrchestratorName() {
        return targetOrchestratorName;
    }

    public String getWorkflowNodeSuffix() {
        return workflowNodeSuffix;
    }

    public DSSLabel getDssLabel() {
        return dssLabel;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public Long getCopyTaskId() {
        return copyTaskId;
    }

    public String getInstanceName() {
        return instanceName;
    }
}

