package com.webank.wedatasphere.dss.datamodel.indicator.vo;

import java.util.Map;
/**
 * @author helong
 * @date 2021/9/16
 */
public class IndicatorContentVO {

    private Long indicatorId;

    /**
     * 0 原子 1 衍生 2 派生 3 复杂 4 自定义
     */
    private Integer indicatorType;

    private Long measureId;

    /**
     * 指标来源信息
     */
    private Map<String,String> sourceInfo;

    private String formula;

    private String business;

    private String businessOwner;

    private String calculation;

    private String calculationOwner;

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
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


    public Map<String, String> getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(Map<String, String> sourceInfo) {
        this.sourceInfo = sourceInfo;
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

    @Override
    public String toString() {
        return "IndicatorContentVO{" +
                "indicatorId=" + indicatorId +
                ", indicatorType=" + indicatorType +
                ", measureId=" + measureId +
                ", sourceInfo=" + sourceInfo +
                ", formula='" + formula + '\'' +
                ", business='" + business + '\'' +
                ", businessOwner='" + businessOwner + '\'' +
                ", calculation='" + calculation + '\'' +
                ", calculationOwner='" + calculationOwner + '\'' +
                '}';
    }
}
