package com.webank.wedatasphere.warehouse.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.wedatasphere.warehouse.dao.domain.common.DssWorkspaceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@TableName("dss_datawarehouse_layer_generalize_rule")
public class DwLayerGeneralizeRule extends DssWorkspaceEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "layer_id")
    private Long layerId;

    // 自动归纳表达式
    private String regex;

    private String identifier;

    @TableField(value = "en_identifier")
    private String enIdentifier;

    @TableField(value = "desc")
    private String description;

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
