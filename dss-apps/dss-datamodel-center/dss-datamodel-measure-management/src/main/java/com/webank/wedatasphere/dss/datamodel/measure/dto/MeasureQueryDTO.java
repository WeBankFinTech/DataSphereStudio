package com.webank.wedatasphere.dss.datamodel.measure.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MeasureQueryDTO {
    private Long id;

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

    private Date createTime;

    private Date updateTime;

    /**
     * 引用次数
     */
    private Integer refCount = 0;

  }
