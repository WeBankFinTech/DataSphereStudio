package com.webank.wedatasphere.dss.datamodel.table.vo;

import lombok.Data;

import java.util.Map;

@Data
public class LabelUpdateVO {

    private String name;

    private String fieldIdentifier;

    /**
     * 标签键值对 json
     */
    private Map<String,String> paramMap;


    private String comment;

    /**
     * 数仓主题格式为： theme_domain_name.theme_name
     */
    private String warehouseThemeName;

    private String owner;

    /**
     * 授权的名字：userName、roleName
     */
    private String principalName;

    private Integer isAvailable;

    /**
     * 英文
     */
    private String warehouseThemeNameEn;
}
