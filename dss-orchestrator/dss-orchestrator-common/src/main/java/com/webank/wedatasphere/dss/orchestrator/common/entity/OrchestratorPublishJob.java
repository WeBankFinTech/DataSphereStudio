package com.webank.wedatasphere.dss.orchestrator.common.entity;

import java.util.Date;

public class OrchestratorPublishJob {

    private Long id;
    private String jobId;
    private String conversionJobJson;
    private Date createTime;
    private Date updateTime;
    private String instanceName;
    private String status;
    private String errorMsg;

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getConversionJobJson() {
        return conversionJobJson;
    }

    public void setConversionJobJson(String conversionJobJson) {
        this.conversionJobJson = conversionJobJson;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
