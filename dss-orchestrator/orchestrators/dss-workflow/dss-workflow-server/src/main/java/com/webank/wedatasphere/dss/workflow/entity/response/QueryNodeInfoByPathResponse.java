package com.webank.wedatasphere.dss.workflow.entity.response;


public class QueryNodeInfoByPathResponse {
    /**
     * 节点所在项目id
     */
    private Long projectId;


    /**
     *(子)工作流ID
     */
    private Long appId;
    /**
     * 编排ID
     */
    private Long orchestratorId;
    private String orchestratorName;
    private String nodeName;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

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

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
