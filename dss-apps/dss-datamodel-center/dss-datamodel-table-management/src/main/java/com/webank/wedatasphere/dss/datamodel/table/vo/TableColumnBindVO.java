package com.webank.wedatasphere.dss.datamodel.table.vo;

import lombok.Data;

@Data
public class TableColumnBindVO {
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


}
