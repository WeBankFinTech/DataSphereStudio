package com.webank.wedatasphere.dss.workflow.entity.request;

import java.util.List;

public class DataDevelopNodeRequest {



    private Long workspaceId;

    private String username;

    // 是否引用模板
    private Boolean isRefTemplate;

    // 是否复用引擎
    private Boolean reuseEngine;

    private Integer pageNow;

    private Integer pageSize;

    // 节点类型名称
    private List<String> nodeTypeNameList;

    // 节点名称
    private List<String> nodeNameList;

    // 模板名称
    private List<String> templateNameList;


    // 工作流名称
    private String orchestratorName;

    // 项目名称
    private List<String> projectNameList;

    // spark.executor.memory
    private String  sparkExecutorMemory;

    // spark.driver.memory
    private String  sparkDriverMemory;

    // spark.conf
    private String  sparkConf;

    // spark.executor.core
    private String  sparkExecutorCore;

    // spark.executor.instances
    private String sparkExecutorInstances;

    private String executeCluster;

    public String getExecuteCluster() {
        return executeCluster;
    }

    public void setExecuteCluster(String executeCluster) {
        this.executeCluster = executeCluster;
    }
    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPageNow() {
        return pageNow;
    }

    public void setPageNow(Integer pageNow) {
        this.pageNow = pageNow;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    public Boolean getRefTemplate() {
        return isRefTemplate;
    }

    public void setRefTemplate(Boolean refTemplate) {
        isRefTemplate = refTemplate;
    }

    public Boolean getReuseEngine() {
        return reuseEngine;
    }

    public void setReuseEngine(Boolean reuseEngine) {
        this.reuseEngine = reuseEngine;
    }

    public List<String> getNodeTypeNameList() {
        return nodeTypeNameList;
    }

    public void setNodeTypeNameList(List<String> nodeTypeNameList) {
        this.nodeTypeNameList = nodeTypeNameList;
    }

    public List<String> getNodeNameList() {
        return nodeNameList;
    }

    public void setNodeNameList(List<String> nodeNameList) {
        this.nodeNameList = nodeNameList;
    }

    public List<String> getTemplateNameList() {
        return templateNameList;
    }

    public void setTemplateNameList(List<String> templateNameList) {
        this.templateNameList = templateNameList;
    }


    public String getOrchestratorName() {
        return orchestratorName;
    }

    public void setOrchestratorName(String orchestratorName) {
        this.orchestratorName = orchestratorName;
    }

    public List<String> getProjectNameList() {
        return projectNameList;
    }

    public void setProjectNameList(List<String> projectNameList) {
        this.projectNameList = projectNameList;
    }


    public String getSparkExecutorMemory() {
        return sparkExecutorMemory;
    }

    public void setSparkExecutorMemory(String sparkExecutorMemory) {
        this.sparkExecutorMemory = sparkExecutorMemory;
    }

    public String getSparkDriverMemory() {
        return sparkDriverMemory;
    }

    public void setSparkDriverMemory(String sparkDriverMemory) {
        this.sparkDriverMemory = sparkDriverMemory;
    }

    public String getSparkConf() {
        return sparkConf;
    }

    public void setSparkConf(String sparkConf) {
        this.sparkConf = sparkConf;
    }

    public String getSparkExecutorCore() {
        return sparkExecutorCore;
    }

    public void setSparkExecutorCore(String sparkExecutorCore) {
        this.sparkExecutorCore = sparkExecutorCore;
    }

    public String getSparkExecutorInstances() {
        return sparkExecutorInstances;
    }

    public void setSparkExecutorInstances(String sparkExecutorInstances) {
        this.sparkExecutorInstances = sparkExecutorInstances;
    }
}
