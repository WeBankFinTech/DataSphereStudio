package com.webank.wedatasphere.dss.common.entity;

public abstract class Alter {

    /**
     * 告警标题，少于100个字符，必填项
     */
    private String alterTitle;

    private String alterInfo;

    private String alterLevel;

    private String alterReceiver;

    protected Alter() {
    }

    protected Alter(String alterTitle, String alterInfo, String alterLevel, String alterReceiver) {
        this.alterTitle = alterTitle;
        this.alterInfo = alterInfo;
        this.alterLevel = alterLevel;
        this.alterReceiver = alterReceiver;
    }

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

    public String getAlterLevel() {
        return alterLevel;
    }

    public void setAlterLevel(String alterLevel) {
        this.alterLevel = alterLevel;
    }

    public String getAlterReceiver() {
        return alterReceiver;
    }

    public void setAlterReceiver(String alterReceiver) {
        this.alterReceiver = alterReceiver;
    }
}
