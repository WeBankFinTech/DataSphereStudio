package com.webank.wedatasphere.dss.datamodel.indicator.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

public class DssDatamodelIndicator {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String fieldIdentifier;

    private String comment;

    /**
     * 数仓主题格式为： theme_domain_name.theme_name
     */
    private String warehouseThemeName;

    private String owner;

    /**
     * 授权的名字：userName、roleName
     */
    private String principalName;

    private Integer isAvailable;

    private Integer isCoreIndicator;

    /**
     * 空：代表所有，如果是逗号分隔的字符串则代表对应的theme的names
     */
    private String themeArea;

    /**
     * 空：代表所有，如果是逗号分隔的字符串则代表对应的layer的names
     */
    private String layerArea;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldIdentifier() {
        return fieldIdentifier;
    }

    public void setFieldIdentifier(String fieldIdentifier) {
        this.fieldIdentifier = fieldIdentifier;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getWarehouseThemeName() {
        return warehouseThemeName;
    }

    public void setWarehouseThemeName(String warehouseThemeName) {
        this.warehouseThemeName = warehouseThemeName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public Integer getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Integer isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getIsCoreIndicator() {
        return isCoreIndicator;
    }

    public void setIsCoreIndicator(Integer isCoreIndicator) {
        this.isCoreIndicator = isCoreIndicator;
    }

    public String getThemeArea() {
        return themeArea;
    }

    public void setThemeArea(String themeArea) {
        this.themeArea = themeArea;
    }

    public String getLayerArea() {
        return layerArea;
    }

    public void setLayerArea(String layerArea) {
        this.layerArea = layerArea;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}