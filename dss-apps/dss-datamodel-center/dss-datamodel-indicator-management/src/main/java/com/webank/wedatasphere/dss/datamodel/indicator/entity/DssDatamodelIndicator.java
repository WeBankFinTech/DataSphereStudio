package com.webank.wedatasphere.dss.datamodel.indicator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class DssDatamodelIndicator {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String fieldIdentifier;

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

    private Integer isCoreIndicator;

    /**
     * 空：代表所有，如果是逗号分隔的字符串则代表对应的theme的names
     */
    private String themeArea;

    private String themeAreaEn;

    /**
     * 空：代表所有，如果是逗号分隔的字符串则代表对应的layer的names
     */
    private String layerArea;

    private String layerAreaEn;

    private Date createTime;

    private Date updateTime;

    private String version;

    /**
     * 数仓主题英文
     */
    private String warehouseThemeNameEn;

}