package com.webank.wedatasphere.dss.orchestrator.common.entity;

import java.util.Date;

public class OrchestratorPublishJob {

    private Long id;
    private String jobId;
    private String conversionJobJson;
    private Date createTime;
    private Date updateTime;
    private String instanceName;
    private Integer status;

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public OrchestratorPublishJob setJobId(String jobId) {
        this.jobId = jobId;
		return this;
    }

    public String getConversionJobJson() {
        return conversionJobJson;
    }

    public OrchestratorPublishJob setConversionJobJson(String conversionJobJson) {
        this.conversionJobJson = conversionJobJson;
		return this;
    }

    public Date getCreatedTime() {
        return createTime;
    }

    public OrchestratorPublishJob setCreatedTime(Date createdTime) {
        this.createTime = createdTime;
		return this;
    }

    public Date getUpdatedTime() {
        return updateTime;
    }

    public OrchestratorPublishJob setUpdatedTime(Date updatedTime) {
        this.updateTime = updatedTime;
        return this;
    }
}
