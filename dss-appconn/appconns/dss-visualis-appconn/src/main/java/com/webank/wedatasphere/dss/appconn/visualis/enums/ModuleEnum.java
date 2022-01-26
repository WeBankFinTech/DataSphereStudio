package com.webank.wedatasphere.dss.appconn.visualis.enums;

import java.util.Arrays;

public enum ModuleEnum {

    DASHBOARD("linkis.appconn.visualis.dashboard", "DASHBOARD组件"),

    DISPLAY("linkis.appconn.visualis.display", "DISPLAY组件"),

    WIDGET("linkis.appconn.visualis.widget", "WIDGET组件"),

    VIEW("linkis.appconn.visualis.view", "VIEW组件");


    private String name;
    private String desc;

    ModuleEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static ModuleEnum getEnum(String name) {
        return Arrays.stream(ModuleEnum.values()).filter(e -> e.getName().equals(name)).findFirst().orElseThrow(NullPointerException::new);
    }

    public String getName() {
        return name;
    }
}
