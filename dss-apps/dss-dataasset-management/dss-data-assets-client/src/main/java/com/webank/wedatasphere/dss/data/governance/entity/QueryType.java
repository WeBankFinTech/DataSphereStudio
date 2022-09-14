package com.webank.wedatasphere.dss.data.governance.entity;


public enum QueryType {
    PRECISE(1),
    FUZZY(0);
    private int code;

    QueryType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }


}
