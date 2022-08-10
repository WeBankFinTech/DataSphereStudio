package com.webank.wedatasphere.dss.common.auditlog;

/**
 *
 *
 */
public enum OperateTypeEnum {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    COPY("copy"),

    ;

    private String name;

    OperateTypeEnum(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
