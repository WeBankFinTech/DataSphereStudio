package com.webank.wedatasphere.dss.datamodel.table.vo;

/**
 * @author helong
 * @date 2021/10/11
 */
public class TableColumnVO {

    private String name;

    private String alias;

    private String type;

    private String comment;

    private Integer isPartitionField;

    private Integer isPrimary;

    private Integer length;

    private String rule;

    /**
     * 0 维度，1 指标 2 度量
     */
    private Integer modelType;

    /**
     * 关联具体模型id信息
     */
    private Long modelId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getIsPartitionField() {
        return isPartitionField;
    }

    public void setIsPartitionField(Integer isPartitionField) {
        this.isPartitionField = isPartitionField;
    }

    public Integer getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Integer isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Integer getModelType() {
        return modelType;
    }

    public void setModelType(Integer modelType) {
        this.modelType = modelType;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    @Override
    public String toString() {
        return "TableColumnVO{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", type='" + type + '\'' +
                ", comment='" + comment + '\'' +
                ", isPartitionField=" + isPartitionField +
                ", isPrimary=" + isPrimary +
                ", length=" + length +
                ", rule='" + rule + '\'' +
                ", modelType=" + modelType +
                ", modelId=" + modelId +
                '}';
    }
}
