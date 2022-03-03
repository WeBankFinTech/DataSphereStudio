package com.webank.wedatasphere.dss.datamodel.dimension.vo;


public class DimensionEnableVO {


    private Integer isAvailable;


    public Integer getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Integer isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "DimensionEnableVO{" +
                "isAvailable=" + isAvailable +
                '}';
    }
}
