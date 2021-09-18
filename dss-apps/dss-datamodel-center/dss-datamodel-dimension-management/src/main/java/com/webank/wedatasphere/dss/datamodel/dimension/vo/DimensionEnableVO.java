package com.webank.wedatasphere.dss.datamodel.dimension.vo;

/**
 * @author helong
 * @date 2021/9/15
 */
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
