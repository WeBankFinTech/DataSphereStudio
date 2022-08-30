package com.webank.wedatasphere.dss.orchestrator.server.entity.request;

import com.webank.wedatasphere.dss.common.label.LabelRouteVO;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class OrchestratorCopyRequest {

    @NotNull(message = "源编排Id不能为空")
    private Long sourceOrchestratorId;

    @NotNull(message = "源编排名称不能为空")
    private String sourceOrchestratorName;

    @NotNull(message = "目标编排名称不能为空")
    private String targetOrchestratorName;

    @NotNull(message = "源工程Id不能为空")
    private Long sourceProjectId;

    @NotNull(message = "目标工程Id不能为空")
    private Long targetProjectId;

    @NotNull(message = "源工程名不能为空")
    private String sourceProjectName;

    @NotNull(message = "目标工程名不能为空")
    private String targetProjectName;

    @NotNull(message = "工作空间Id不能为空")
    private Long workspaceId;

    @NotNull(message = "目标工作流节点后缀")
    private String workflowNodeSuffix;

    private LabelRouteVO labels;

    public LabelRouteVO getLabels() {
        return labels;
    }

    public void setLabels(LabelRouteVO labels) {
        this.labels = labels;
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

    public Long getSourceProjectId() {
        return sourceProjectId;
    }

    public void setSourceProjectId(Long sourceProjectId) {
        this.sourceProjectId = sourceProjectId;
    }

    public Long getTargetProjectId() {
        return targetProjectId;
    }

    public void setTargetProjectId(Long targetProjectId) {
        this.targetProjectId = targetProjectId;
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

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getWorkflowNodeSuffix() {
        return workflowNodeSuffix;
    }

    public void setWorkflowNodeSuffix(String workflowNodeSuffix) {
        this.workflowNodeSuffix = workflowNodeSuffix;
    }

    @Override
    public String toString() {
        return "OrchestratorCopyRequest{" +
                "sourceOrchestratorId=" + sourceOrchestratorId +
                ", sourceOrchestratorName='" + sourceOrchestratorName + '\'' +
                ", targetOrchestratorName='" + targetOrchestratorName + '\'' +
                ", sourceProjectId=" + sourceProjectId +
                ", targetProjectId=" + targetProjectId +
                ", sourceProjectName='" + sourceProjectName + '\'' +
                ", targetProjectName='" + targetProjectName + '\'' +
                ", workspaceId=" + workspaceId +
                ", workflowNodeSuffix='" + workflowNodeSuffix + '\'' +
                ", labels=" + labels +
                '}';
    }
}
