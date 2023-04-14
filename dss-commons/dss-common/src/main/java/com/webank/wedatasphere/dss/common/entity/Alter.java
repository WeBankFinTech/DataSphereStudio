package com.webank.wedatasphere.dss.common.entity;

public abstract class Alter {

    private String alterTitle;

    private String alterInfo;

    public String getAlterTitle() {
        return alterTitle;
    }

    public void setAlterTitle(String alterTitle) {
        this.alterTitle = alterTitle;
    }

    public String getAlterInfo() {
        return alterInfo;
    }

    public void setAlterInfo(String alterInfo) {
        this.alterInfo = alterInfo;
    }
}
