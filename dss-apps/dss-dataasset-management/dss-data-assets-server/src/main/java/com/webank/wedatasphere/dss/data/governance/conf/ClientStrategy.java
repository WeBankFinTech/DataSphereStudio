package com.webank.wedatasphere.dss.data.governance.conf;


public enum ClientStrategy {
    TOKEN("token"),
    STATIC("static");

    private final String code;

    ClientStrategy(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
