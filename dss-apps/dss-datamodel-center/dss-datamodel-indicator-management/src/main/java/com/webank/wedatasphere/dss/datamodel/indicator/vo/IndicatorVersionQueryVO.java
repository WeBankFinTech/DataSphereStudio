package com.webank.wedatasphere.dss.datamodel.indicator.vo;


public class IndicatorVersionQueryVO {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IndicatorVersionQueryVO{" +
                "name='" + name + '\'' +
                '}';
    }
}
