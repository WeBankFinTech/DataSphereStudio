package com.webank.wedatasphere.dss.datamodel.center.common.config;

import lombok.Data;


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
