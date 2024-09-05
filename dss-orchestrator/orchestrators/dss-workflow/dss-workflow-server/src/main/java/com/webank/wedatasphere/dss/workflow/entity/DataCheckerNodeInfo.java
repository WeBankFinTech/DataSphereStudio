package com.webank.wedatasphere.dss.workflow.entity;

import java.util.Date;
import java.util.Map;

public class DataCheckerNodeInfo {

    // 项目名称
    private String projectName;

    // 工作流名称
    private String orchestratorName;

    // 工作流ID

    private Long orchestratorId;

    // 节点名称
    private String nodeName;

    // 节点类型
    private String nodeType;

    private String nodeKey;

    // 节点小类
    private String nodeTypeName;

    // 节点ID
    private String nodeId;

    // 节点描述
    private String nodeDesc;

    // source.type
    private String sourceType;

    // check.object
    private String checkObject;

    // max.check.hours
    private String maxCheckHours;

    //job.desc
    private String jobDesc;

    // qualities 校验
    private Boolean qualitisCheck;


    private  Long projectId;

    private Date updateTime;

    private Date createTime;

    private Map<String,String> nodeContent;

    // 节点配置ID
    private Long contentId;

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

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeDesc() {
        return nodeDesc;
    }

    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCheckObject() {
        return checkObject;
    }

    public void setCheckObject(String checkObject) {
        this.checkObject = checkObject;
    }

    public String getMaxCheckHours() {
        return maxCheckHours;
    }

    public void setMaxCheckHours(String maxCheckHours) {
        this.maxCheckHours = maxCheckHours;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public Boolean getQualitisCheck() {
        return qualitisCheck;
    }

    public void setQualitisCheck(Boolean qualitisCheck) {
        this.qualitisCheck = qualitisCheck;
    }

    public Long getOrchestratorId() {
        return orchestratorId;
    }

    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
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

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
}
