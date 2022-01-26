package com.webank.wedatasphere.dss.appconn.visualis.enums;

import java.util.Arrays;

/**
 * Description
 *
 * @Author elishazhang
 * @Date 2021/10/20
 */

public enum NodeIdEnum {

    DASHBOARD_PORTAL_IDS("dashboardPortalIds", "DASHBOARD ids"),

    DISPLAY_IDS("displayIds", "DISPLAY ids"),

    WIDGET_IDS("widgetIds", "WIDGET ids"),

    VIEW_IDS("viewIds", "VIEW ids");


    private String name;
    private String desc;

    NodeIdEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static NodeIdEnum getEnum(String name) {
        return Arrays.stream(NodeIdEnum.values()).filter(e -> e.getName().equals(name)).findFirst().orElseThrow(NullPointerException::new);
    }

    public String getName() {
        return name;
    }
}
