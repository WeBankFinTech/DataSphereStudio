package com.webank.wedatasphere.dss.datamodel.table.entity;

import java.util.Date;

public class DssDatamodelTableMaterializedHistory {
    private Long id;

    /**
     * 物化sql
     */
    private String materializedCode;

    /**
     * 物化原因
     */
    private String reason;

    /**
     * 物化者
     */
    private String creator;

    /**
     * succeed,failed,in progess
     */
    private Integer status;

    private Date createTime;

    private Date lastUpdateTime;

    private String taskId;

    private String errorMsg;

    /**
     * 表名
     */
    private String tablename;

    private String database;

    /**
     * 版本信息：默认1
     */
    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaterializedCode() {
        return materializedCode;
    }

    public void setMaterializedCode(String materializedCode) {
        this.materializedCode = materializedCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}