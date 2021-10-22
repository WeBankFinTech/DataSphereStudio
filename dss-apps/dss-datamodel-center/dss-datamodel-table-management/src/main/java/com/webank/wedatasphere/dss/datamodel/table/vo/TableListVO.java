package com.webank.wedatasphere.dss.datamodel.table.vo;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TableListVO {

    private Integer pageSize = 20;

    private Integer pageNum = 1;



    private String name;

    /**
     * 数仓层级
     */
    private String warehouseLayerName;

    /**
     * 数仓主题格式为： theme_domain_name.theme_name
     */
    private String warehouseThemeName;


    private String user;

    /**
     * 0 维度 1 指标 2 度量  -1 按照表名查询
     */
    private Integer modelType = -1;


    private String modelName;

}
