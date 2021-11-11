package com.webank.wedatasphere.warehouse.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.wedatasphere.warehouse.dao.domain.common.DssWorkspaceEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dss_datawarehouse_modifier")
public class DwModifier extends DssWorkspaceEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "modifier_type")
    private String modifierType;

    @TableField(value = "modifier_type_en")
    private String modifierTypeEn;

    @TableField(value = "theme_domain_id")
    private Long themeDomainId;

    // 空：代表所有，如果是逗号分隔的字符串则代表对应的theme的names
    @TableField(value = "theme_area")
    private String themeArea;

    @TableField(value = "layer_id")
    private Long layerId;

    // 空：代表所有，如果是逗号分隔的字符串则代表对应的layer的names
    @TableField(value = "layer_area")
    private String layerArea;

    private String description;

    @TableField(value = "is_available")
    private Boolean isAvailable;

//    @TableField(value = "create_user")
//    private String createUser;

    @TableField(value = "create_time")
    private Date createTime;

//    @TableField(value = "modify_user")
//    private String modifyUser;

    @TableField(value = "update_time")
    private Date updateTime;

    private Boolean status;

    @TableField(value = "lock_version")
    private Long lockVersion;

}
