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

}
