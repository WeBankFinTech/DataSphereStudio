package com.webank.wedatasphere.dss.workflow.entity.response;

import java.util.List;

public class BatchEditNodeContentResponse {

    private Long orchestratorId;
    private String orchestratorName;

    private List<String> failNodeName;
    private List<String> successNodeName;

    private String errorMsg;
    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }


    public List<String> getFailNodeName() {
        return failNodeName;
    }

    public void setFailNodeName(List<String> failNodeName) {
        this.failNodeName = failNodeName;
    }

    public List<String> getSuccessNodeName() {
        return successNodeName;
    }

    public void setSuccessNodeName(List<String> successNodeName) {
        this.successNodeName = successNodeName;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
