package com.webank.wedatasphere.dss.datamodel.table.dto;


import java.util.Date;

public class TableVersionQueryDTO {

    private Long id;

    private Long tblId;

    private String name;

    /**
     * 是否物化
     */
    private Integer isMaterialized;

    /**
     * 创建table的sql
     */
    private String tableCode;

    /**
     * 版本注释
     */
    private String comment;

    /**
     * 版本信息：默认 1
     */
    private String version;

    private String tableParams;

    private String columns;

    /**
     * rollback,update,add
     */
    private String sourceType;

    private Date createTime;

    private Date updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTblId() {
        return tblId;
    }

    public void setTblId(Long tblId) {
        this.tblId = tblId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsMaterialized() {
        return isMaterialized;
    }

    public void setIsMaterialized(Integer isMaterialized) {
        this.isMaterialized = isMaterialized;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTableParams() {
        return tableParams;
    }

    public void setTableParams(String tableParams) {
        this.tableParams = tableParams;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
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
}
