package com.webank.wedatasphere.dss.datamodel.table.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class ModelTypeDTO {

    /**
     * 0 维度，1 指标 2 度量
     */
    private Integer modelType;

    /**
     * 模型信息英文名称
     */
    private String modelNameEn;


    /**
     * 模型信息名称
     */
    private String modelName;
}
