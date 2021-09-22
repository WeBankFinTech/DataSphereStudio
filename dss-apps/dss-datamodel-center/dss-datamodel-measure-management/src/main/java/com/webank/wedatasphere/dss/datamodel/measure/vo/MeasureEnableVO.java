package com.webank.wedatasphere.dss.datamodel.measure.vo;


public class MeasureEnableVO {


    private Integer isAvailable;

    public Integer getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Integer isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "MeasureEnableVO{" +
                "isAvailable=" + isAvailable +
                '}';
    }
}
