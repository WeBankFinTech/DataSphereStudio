package com.webank.wedatasphere.dss.datamodel.indicator.vo;


public class IndicatorEnableVO {

    private Integer isAvailable;


    public Integer getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Integer isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "IndicatorEnableVO{" +
                "isAvailable=" + isAvailable +
                '}';
    }
}
