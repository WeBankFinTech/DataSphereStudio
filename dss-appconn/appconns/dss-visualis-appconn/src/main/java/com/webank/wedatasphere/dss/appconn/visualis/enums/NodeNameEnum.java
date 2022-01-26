package com.webank.wedatasphere.dss.appconn.visualis.enums;

import java.util.Arrays;

/**
 * Description
 *
 * @Author elishazhang
 * @Date 2021/10/20
 */

public enum NodeNameEnum {

    DASHBOARD_NAME("dashboardPortal", "DASHBOARD节点名称"),

    DISPLAY_NAME("display", "DISPLAY节点名称"),

    WIDGET_NAME("widget", "WIDGET节点名称"),

    VIEW_NAME("view", "VIEW节点名称");


    private String name;
    private String desc;

    NodeNameEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static NodeNameEnum getEnum(String name) {
        return Arrays.stream(NodeNameEnum.values()).filter(e -> e.getName().equals(name)).findFirst().orElseThrow(NullPointerException::new);
    }

    public String getName() {
        return name;
    }
}
