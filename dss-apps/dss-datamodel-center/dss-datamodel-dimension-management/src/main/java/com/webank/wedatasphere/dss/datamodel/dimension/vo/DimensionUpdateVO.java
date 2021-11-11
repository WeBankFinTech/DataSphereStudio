package com.webank.wedatasphere.dss.datamodel.dimension.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DimensionUpdateVO {

    private String name;

    private String fieldIdentifier;

    private String formula;

    private String comment;

    /**
     * 数仓主题格式为： theme_domain_name.theme_name
     */
    private String warehouseThemeName;

    private String warehouseThemeNameEn;

    private String owner;

    /**
     * 授权的名字：userName、roleName
     */
    private String principalName;

    private Integer isAvailable;


}
