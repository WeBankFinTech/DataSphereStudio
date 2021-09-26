package com.webank.wedatasphere.dss.datamodel.indicator.entity;

import java.util.Date;


public class DssDatamodelIndicatorQuery {

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

    private Date createTime;

    private Date updateTime;

    private String version;

    /**
     * 0 原子 1 衍生 2 派生 3 复杂 4 自定义
     */
    private Integer indicatorType;

    private Long measureId;

    /**
     * 指标来源信息
     */
    private String indicatorSourceInfo;

    private String formula;

    private String business;

    private String businessOwner;

    private String calculation;

    private String calculationOwner;

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

    public Integer getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(Integer indicatorType) {
        this.indicatorType = indicatorType;
    }

    public Long getMeasureId() {
        return measureId;
    }

    public void setMeasureId(Long measureId) {
        this.measureId = measureId;
    }

    public String getIndicatorSourceInfo() {
        return indicatorSourceInfo;
    }

    public void setIndicatorSourceInfo(String indicatorSourceInfo) {
        this.indicatorSourceInfo = indicatorSourceInfo;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getBusinessOwner() {
        return businessOwner;
    }

    public void setBusinessOwner(String businessOwner) {
        this.businessOwner = businessOwner;
    }

    public String getCalculation() {
        return calculation;
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
    }

    public String getCalculationOwner() {
        return calculationOwner;
    }

    public void setCalculationOwner(String calculationOwner) {
        this.calculationOwner = calculationOwner;
    }
}
