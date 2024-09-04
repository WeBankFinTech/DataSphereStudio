package com.webank.wedatasphere.dss.workflow.entity;

import java.util.Date;
import java.util.Map;

public class DataDevelopNodeInfo {

    // 项目名称
    private String projectName;

    // 工作流名称
    private String orchestratorName;

    // 节点名称
    private String nodeName;

    // 节点类型

    private String nodeType;

    // 节点小类
    private String nodeTypeName;

    // 是否引用参数模板
    private Boolean isRefTemplate;

    // 模板名称
    private String templateName;

    // 资源名称
    private String resource;

    //节点ID
    private String nodeId;

    // 工作流ID
    private Long orchestratorId;

    // 节点配置ID
    private Long contentId;

    private Long projectId;

    // 是否服用引擎
    private Boolean reuseEngine;

    private Date updateTime;

    private Date createTime;

    private String nodeKey;

    private  Map<String,String> nodeContent;
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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


    public Boolean getRefTemplate() {
        return isRefTemplate;
    }

    public void setRefTemplate(Boolean refTemplate) {
        isRefTemplate = refTemplate;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getNodeTypeName() {
        return nodeTypeName;
    }

    public void setNodeTypeName(String nodeTypeName) {
        this.nodeTypeName = nodeTypeName;
    }

    public Boolean getReuseEngine() {
        return reuseEngine;
    }

    public void setReuseEngine(Boolean reuseEngine) {
        this.reuseEngine = reuseEngine;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public Map<String, String> getNodeContent() {
        return nodeContent;
    }

    public void setNodeContent(Map<String, String> nodeContent) {
        this.nodeContent = nodeContent;
    }
}
