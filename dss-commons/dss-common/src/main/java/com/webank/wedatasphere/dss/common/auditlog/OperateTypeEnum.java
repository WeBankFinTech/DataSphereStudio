package com.webank.wedatasphere.dss.common.auditlog;

/**
 * 审计日志操作类型枚举
 * Author: xlinliu
 * Date: 2022/8/10
 */
public enum OperateTypeEnum {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    COPY("copy"),
    PUBLISH("publish"),
    DISABLE("disable"),
    ENABLE("enable"),
    ADD_TO_FAVORITES("add_to_favorites"),
    REM_FROM_FAVORITES("rem_from_favorites"),
    UPDATE_ROLE_MENU("update_role_menu"),
    UPDATE_ROLE_COMPONENT("update_role_component"),
    ADD_USERS("add_users"),
    UPDATE_USERS("update_users"),
    KILL("kill"),
    SEND_EMAIL("send_email"),


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
