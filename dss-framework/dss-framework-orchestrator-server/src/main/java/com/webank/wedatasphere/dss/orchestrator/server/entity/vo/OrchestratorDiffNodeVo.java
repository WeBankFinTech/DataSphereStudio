package com.webank.wedatasphere.dss.orchestrator.server.entity.vo;

import java.util.List;

public class OrchestratorDiffNodeVo {

    private String orchestratorName;

    private Long orchestratorId;

    private List<String> nodeContainsKeywordsNodeList;


    public OrchestratorDiffNodeVo() {
    }

    public OrchestratorDiffNodeVo(String orchestratorName, Long orchestratorId, List<String> nodeContainsKeywordsNodeList) {
        this.orchestratorName = orchestratorName;
        this.orchestratorId = orchestratorId;
        this.nodeContainsKeywordsNodeList = nodeContainsKeywordsNodeList;
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

    public List<String> getNodeContainsKeywordsNodeList() {
        return nodeContainsKeywordsNodeList;
    }

    public void setNodeContainsKeywordsNodeList(List<String> nodeContainsKeywordsNodeList) {
        this.nodeContainsKeywordsNodeList = nodeContainsKeywordsNodeList;
    }
}
