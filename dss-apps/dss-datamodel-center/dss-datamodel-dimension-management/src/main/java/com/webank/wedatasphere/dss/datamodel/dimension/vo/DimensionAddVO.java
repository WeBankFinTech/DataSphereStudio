package com.webank.wedatasphere.dss.datamodel.dimension.vo;

import com.webank.wedatasphere.dss.datamodel.dimension.entity.DssDatamodelDimension;

/**
 * @author helong
 * @date 2021/9/14
 */
public class DimensionAddVO {

    private String name;

    private String fieldIdentifier;

    private String formula;

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

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
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

    @Override
    public String toString() {
        return "DimensionAddVO{" +
                "name='" + name + '\'' +
                ", fieldIdentifier='" + fieldIdentifier + '\'' +
                ", formula='" + formula + '\'' +
                ", comment='" + comment + '\'' +
                ", warehouseThemeName='" + warehouseThemeName + '\'' +
                ", owner='" + owner + '\'' +
                ", principalName='" + principalName + '\'' +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
