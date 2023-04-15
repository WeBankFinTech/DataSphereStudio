package com.webank.wedatasphere.dss.common.entity;

public class CustomAlter extends Alter{

    private String alterLevel;

    private String alterReceiver;

    public CustomAlter() {
    }

    public CustomAlter(String alterTitle, String alterInfo, String alterLevel, String alterReceiver) {
        super(alterTitle, alterInfo);
        this.alterLevel = alterLevel;
        this.alterReceiver = alterReceiver;
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
