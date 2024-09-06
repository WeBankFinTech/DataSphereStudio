package com.webank.wedatasphere.dss.workflow.entity;

import java.util.Date;
import java.util.Map;

public class DataViewNodeInfo {

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

    // 节点描述
    private String nodeDesc;

    // 视图ID
    private String viewId;

    //节点ID
    private String nodeId;

    //节点配置ID
    private Long contentId;


    // 工作流ID
    private Long orchestratorId;

    private  Long projectId;

    private Date updateTime;

    private Date createTime;

    private String nodeKey;

    private Map<String,String> nodeContent;

    /**
     * 1-接入git 0-不接入（默认）
     */
    private Boolean associateGit;
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

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeTypeName() {
        return nodeTypeName;
    }

    public void setNodeTypeName(String nodeTypeName) {
        this.nodeTypeName = nodeTypeName;
    }

    public String getNodeDesc() {
        return nodeDesc;
    }

    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
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

    public Boolean getAssociateGit() {
        return associateGit;
    }

    public void setAssociateGit(Boolean associateGit) {
        this.associateGit = associateGit;
    }
}
