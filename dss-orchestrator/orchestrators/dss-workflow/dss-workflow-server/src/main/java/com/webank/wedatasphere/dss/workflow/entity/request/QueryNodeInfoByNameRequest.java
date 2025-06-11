package com.webank.wedatasphere.dss.workflow.entity.request;

import java.util.List;

public class QueryNodeInfoByNameRequest {

    private Long orchestratorId;

    private List<String> nodeNameList;


    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public List<String> getNodeNameList() {
        return nodeNameList;
    }

    public void setNodeNameList(List<String> nodeNameList) {
        this.nodeNameList = nodeNameList;
    }


    @Override
    public String toString() {
        return "QueryNodeInfoByNameRequest{" +
                "orchestratorId=" + orchestratorId +
                ", nodeNameList=" + nodeNameList +
                '}';
    }
}
