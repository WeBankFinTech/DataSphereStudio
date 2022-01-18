package com.webank.wedatasphere.dss.datamodel.table.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TableColumnQueryDTO {

    private Long id;

    private Long tableId;

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
     * 模型信息名称
     */
    private String modelName;

    /**
     * 模型信息英文名称
     */
    private String modelNameEn;

    private Date createTime;

    private Date updateTime;


}
