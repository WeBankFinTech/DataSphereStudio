package com.webank.wedatasphere.dss.datamodel.table.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

public class DssDatamodelTableStatics {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String originTables;

    private Long tableId;

    private Integer accessCount;

    private Integer lastAccessTime;

    /**
    * 存储10行用例数据
    */
    private String sampleDataPath;

    private Integer sampleUpdateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginTables() {
        return originTables;
    }

    public void setOriginTables(String originTables) {
        this.originTables = originTables;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public Integer getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(Integer accessCount) {
        this.accessCount = accessCount;
    }

    public Integer getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Integer lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getSampleDataPath() {
        return sampleDataPath;
    }

    public void setSampleDataPath(String sampleDataPath) {
        this.sampleDataPath = sampleDataPath;
    }

    public Integer getSampleUpdateTime() {
        return sampleUpdateTime;
    }

    public void setSampleUpdateTime(Integer sampleUpdateTime) {
        this.sampleUpdateTime = sampleUpdateTime;
    }
}