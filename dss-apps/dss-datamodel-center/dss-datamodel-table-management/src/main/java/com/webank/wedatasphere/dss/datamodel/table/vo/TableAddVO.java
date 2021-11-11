package com.webank.wedatasphere.dss.datamodel.table.vo;


import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
public class TableAddVO {

    private String dataBase;

    private String name;

    private String alias;

    private String creator;

    private String comment;


    /**
     * 是否外部表 0 内部表 1外部表
     */
    private Integer isExternal;

    /**
     * 数仓层级
     */
    private String warehouseLayerName;

    /**
     * 数仓层级英文
     */
    private String warehouseLayerNameEn;

    /**
     * 数仓主题格式为： theme_domain_name.theme_name
     */
    private String warehouseThemeName;

    /**
     * 数仓主题英文
     */
    private String warehouseThemeNameEn;

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

    /**
     * 外部表时 location
     */
    private String location;

    /**
     * 标签
     */
    private String label;

    private List<TableColumnVO> columns;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(Integer isExternal) {
        this.isExternal = isExternal;
    }

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
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
