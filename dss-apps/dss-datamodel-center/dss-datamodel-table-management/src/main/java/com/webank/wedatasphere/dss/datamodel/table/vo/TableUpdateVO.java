package com.webank.wedatasphere.dss.datamodel.table.vo;

import java.util.Date;
import java.util.List;


public class TableUpdateVO {


    private String database;

    private String name;

    private String alias;

    private String creator;

    private String comment;

    private Date createTime;

    private Date updateTime;

    /**
     * 数仓层级
     */
    private String warehouseLayerName;

    /**
     * 数仓主题格式为： theme_domain_name.theme_name
     */
    private String warehouseThemeName;

    /**
     * 生命周期
     */
    private String lifecycle;

    private Integer isPartitionTable;

    private Integer isAvailable;

    /**
     * 存储类型：hive/mysql
     */
    private String storageType;

    /**
     * 授权的名字：userName、roleName
     */
    private String principalName;

    /**
     * 压缩格式
     */
    private String compress;

    /**
     * 文件格式
     */
    private String fileType;


    private List<TableColumnVO> columns;


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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getWarehouseLayerName() {
        return warehouseLayerName;
    }

    public void setWarehouseLayerName(String warehouseLayerName) {
        this.warehouseLayerName = warehouseLayerName;
    }

    public String getWarehouseThemeName() {
        return warehouseThemeName;
    }

    public void setWarehouseThemeName(String warehouseThemeName) {
        this.warehouseThemeName = warehouseThemeName;
    }

    public String getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(String lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Integer getIsPartitionTable() {
        return isPartitionTable;
    }

    public void setIsPartitionTable(Integer isPartitionTable) {
        this.isPartitionTable = isPartitionTable;
    }

    public Integer getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Integer isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getCompress() {
        return compress;
    }

    public void setCompress(String compress) {
        this.compress = compress;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public List<TableColumnVO> getColumns() {
        return columns;
    }

    public void setColumns(List<TableColumnVO> columns) {
        this.columns = columns;
    }
}
