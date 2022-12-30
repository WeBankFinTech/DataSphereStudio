package com.webank.wedatasphere.warehouse.domain;

//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

//@Setter
//@Getter
//@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DwStatisticalPeriodVO {
    private Long id;

    private Long themeDomainId;

    private Long layerId;

    private String name;

    private String enName;

    private String description;

    private String startTimeFormula;

    private String endTimeFormula;

    private String principalName;

    private Boolean isAvailable;

    private String owner;

    private Date createTime;

    private Date updateTime;

    private String themeArea;

    private String themeAreaEn;

    private String layerArea;

    private String layerAreaEn;

    transient private Boolean status;

    private Boolean referenced;

    private int referenceCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getThemeDomainId() {
        return themeDomainId;
    }

    public void setThemeDomainId(Long themeDomainId) {
        this.themeDomainId = themeDomainId;
    }

    public Long getLayerId() {
        return layerId;
    }

    public void setLayerId(Long layerId) {
        this.layerId = layerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTimeFormula() {
        return startTimeFormula;
    }

    public void setStartTimeFormula(String startTimeFormula) {
        this.startTimeFormula = startTimeFormula;
    }

    public String getEndTimeFormula() {
        return endTimeFormula;
    }

    public void setEndTimeFormula(String endTimeFormula) {
        this.endTimeFormula = endTimeFormula;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getThemeArea() {
        return themeArea;
    }

    public void setThemeArea(String themeArea) {
        this.themeArea = themeArea;
    }

    public String getThemeAreaEn() {
        return themeAreaEn;
    }

    public void setThemeAreaEn(String themeAreaEn) {
        this.themeAreaEn = themeAreaEn;
    }

    public String getLayerArea() {
        return layerArea;
    }

    public void setLayerArea(String layerArea) {
        this.layerArea = layerArea;
    }

    public String getLayerAreaEn() {
        return layerAreaEn;
    }

    public void setLayerAreaEn(String layerAreaEn) {
        this.layerAreaEn = layerAreaEn;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getReferenced() {
        return referenced;
    }

    public void setReferenced(Boolean referenced) {
        this.referenced = referenced;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(int referenceCount) {
        this.referenceCount = referenceCount;
    }
}
