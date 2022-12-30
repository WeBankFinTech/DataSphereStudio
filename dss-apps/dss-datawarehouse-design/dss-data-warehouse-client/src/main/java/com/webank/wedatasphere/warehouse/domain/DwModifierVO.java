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
public class DwModifierVO {
    private Long id;
    private String modifierType;
    private String modifierTypeEn;
    private String layerArea;
    private String layerAreaEn;
    private String themeArea;
    private String themeAreaEn;
    private String description;
    private Boolean isAvailable;
    private Date createTime;
    private Date updateTime;
    private int referenceCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModifierType() {
        return modifierType;
    }

    public void setModifierType(String modifierType) {
        this.modifierType = modifierType;
    }

    public String getModifierTypeEn() {
        return modifierTypeEn;
    }

    public void setModifierTypeEn(String modifierTypeEn) {
        this.modifierTypeEn = modifierTypeEn;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
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

    public int getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(int referenceCount) {
        this.referenceCount = referenceCount;
    }
}
