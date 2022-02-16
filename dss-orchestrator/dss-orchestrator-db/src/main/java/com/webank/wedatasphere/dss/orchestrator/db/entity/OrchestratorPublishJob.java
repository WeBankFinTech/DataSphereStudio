package com.webank.wedatasphere.dss.orchestrator.db.entity;

import java.util.Date;

public class OrchestratorPublishJob {

    private Long id;
    private String jobId;
    private String conversionJobJson;
    private Date createdTime;
    private Date updatedTime;

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
        return createdTime;
    }

    public OrchestratorPublishJob setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
		return this;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public OrchestratorPublishJob setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }
}
