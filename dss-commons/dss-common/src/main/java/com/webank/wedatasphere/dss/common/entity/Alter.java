package com.webank.wedatasphere.dss.common.entity;

public abstract class Alter {

    /**
     * 告警标题，少于100个字符，必填项
     */
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
