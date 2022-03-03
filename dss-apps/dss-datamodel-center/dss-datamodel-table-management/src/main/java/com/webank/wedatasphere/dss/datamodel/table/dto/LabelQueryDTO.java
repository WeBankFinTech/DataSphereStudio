package com.webank.wedatasphere.dss.datamodel.table.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class LabelQueryDTO {

    private Long id;

    private String name;

    private String fieldIdentifier;

    /**
     * 标签键值对 json
     */
    private String params;

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

    private Date createTime;

    private Date updateTime;

    /**
     * 英文
     */
    private String warehouseThemeNameEn;

    /**
     * 被引用次数
     */
    private Integer refCount =0;
}
