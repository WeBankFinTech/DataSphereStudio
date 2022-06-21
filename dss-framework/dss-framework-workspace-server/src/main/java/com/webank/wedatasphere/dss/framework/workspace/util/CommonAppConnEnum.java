package com.webank.wedatasphere.dss.framework.workspace.util;

public enum CommonAppConnEnum {
    /**
     * 工作空间默认组件
     * 需要和dss_appconn插入的name一致
     */
    WORKFLOW("workflow"),
    SCRIPTIS("scriptis");

    private String name;

    CommonAppConnEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
