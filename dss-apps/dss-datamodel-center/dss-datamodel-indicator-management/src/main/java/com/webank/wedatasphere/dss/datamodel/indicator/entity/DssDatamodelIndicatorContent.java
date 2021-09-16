package com.webank.wedatasphere.dss.datamodel.indicator.entity;

import java.util.Date;

public class DssDatamodelIndicatorContent {
    private Long id;

    private Long indicatorId;

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

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
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