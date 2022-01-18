package com.webank.wedatasphere.dss.datamodel.indicator.vo;


public class IndicatorVersionRollBackVO {

    private String name;

    private String version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "VersionRollBackVO{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
