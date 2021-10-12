package com.webank.wedatasphere.dss.datamodel.table.entity;

import java.util.Date;

public class DssDatamodelTableStats {
    private Long id;

    private String database;

    private String name;

    private Date createTime;

    private Date updateTime;

    /**
     * 字段数
     */
    private Integer columnCount;

    /**
     * 存储大小
     */
    private Integer storageSize;

    /**
     * 文件数
     */
    private Integer fileCount;

    /**
     * 分区数
     */
    private Integer partitionCount;

    /**
     * 访问次数
     */
    private Integer accessCount;

    /**
     * 收藏次数
     */
    private Integer collectCount;

    /**
     * 引用次数
     */
    private Integer refCount;

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

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
    }

    public Integer getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(Integer storageSize) {
        this.storageSize = storageSize;
    }

    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    public Integer getPartitionCount() {
        return partitionCount;
    }

    public void setPartitionCount(Integer partitionCount) {
        this.partitionCount = partitionCount;
    }

    public Integer getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(Integer accessCount) {
        this.accessCount = accessCount;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public Integer getRefCount() {
        return refCount;
    }

    public void setRefCount(Integer refCount) {
        this.refCount = refCount;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}