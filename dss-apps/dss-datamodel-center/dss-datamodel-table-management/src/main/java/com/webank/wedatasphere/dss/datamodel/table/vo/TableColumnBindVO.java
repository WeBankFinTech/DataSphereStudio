package com.webank.wedatasphere.dss.datamodel.table.vo;


public class TableColumnBindVO {
    /**
     * 0 维度，1 指标 2 度量
     */
    private Integer modelType;

    /**
     * 模型信息名称
     */
    private String modelName;


    public Integer getModelType() {
        return modelType;
    }

    public void setModelType(Integer modelType) {
        this.modelType = modelType;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String toString() {
        return "TableColumnBindVO{" +
                "modelType=" + modelType +
                ", modelName='" + modelName + '\'' +
                '}';
    }
}
