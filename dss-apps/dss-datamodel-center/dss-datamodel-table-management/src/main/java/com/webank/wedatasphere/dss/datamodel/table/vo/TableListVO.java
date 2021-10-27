package com.webank.wedatasphere.dss.datamodel.table.vo;


import com.webank.wedatasphere.dss.datamodel.center.common.vo.PageVO;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TableListVO extends PageVO {

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
