package com.webank.wedatasphere.dss.datamodel.table.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
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


    private String modelName;


}
