package com.webank.wedatasphere.dss.framework.workspace.util;

public enum CommonMenuEnum {
    /**
     * 中文名需要和数据库插入的一致
     */
    APPLICATION_DEVELOPMENT("应用开发"),
    DATA_ANALYSIS("数据分析"),
    PRODUCTION_OPERATION("生产运维"),
    DATA_QUALITY("数据质量"),
    DATA_EXCHANGE("数据交换"),
    DATA_APPLICATION("数据应用"),
    ADMIN_FUNCTOIN("管理员功能");

    CommonMenuEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

}
