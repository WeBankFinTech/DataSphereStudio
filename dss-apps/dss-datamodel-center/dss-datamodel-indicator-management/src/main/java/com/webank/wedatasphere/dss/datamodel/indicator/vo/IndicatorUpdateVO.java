package com.webank.wedatasphere.dss.datamodel.indicator.vo;


public class IndicatorUpdateVO {

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


    private IndicatorContentVO content;


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

    public IndicatorContentVO getContent() {
        return content;
    }

    public void setContent(IndicatorContentVO content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "IndicatorAddVO{" +
                "name='" + name + '\'' +
                ", fieldIdentifier='" + fieldIdentifier + '\'' +
                ", comment='" + comment + '\'' +
                ", warehouseThemeName='" + warehouseThemeName + '\'' +
                ", owner='" + owner + '\'' +
                ", principalName='" + principalName + '\'' +
                ", isAvailable=" + isAvailable +
                ", isCoreIndicator=" + isCoreIndicator +
                ", themeArea='" + themeArea + '\'' +
                ", layerArea='" + layerArea + '\'' +
                ", content=" + content +
                '}';
    }
}
