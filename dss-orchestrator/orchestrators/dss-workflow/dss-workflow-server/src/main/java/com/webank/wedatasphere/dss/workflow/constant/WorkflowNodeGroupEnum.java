package com.webank.wedatasphere.dss.workflow.constant;

public enum WorkflowNodeGroupEnum {
    DataDevelopment("数据开发","Data development"),
    DataVisualization("数据可视化","Data visualization"),
    SignalNode("信号节点","Signal node");

    private String name;
    private String nameEn;


    WorkflowNodeGroupEnum(String name, String nameEn) {
        this.name = name;
        this.nameEn = nameEn;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }
}
