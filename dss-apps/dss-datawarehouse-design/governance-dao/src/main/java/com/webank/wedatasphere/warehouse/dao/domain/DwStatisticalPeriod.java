package com.webank.wedatasphere.warehouse.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.wedatasphere.warehouse.dao.domain.common.DssWorkspaceEntity;
import com.webank.wedatasphere.warehouse.dao.interceptor.NameAttachWorkspaceTrans;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@TableName("dss_datawarehouse_statistical_period")
public class DwStatisticalPeriod extends DssWorkspaceEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "theme_domain_id")
    private Long themeDomainId;

//    @TableField(value = "theme_area")
//    private String themeArea;

    @TableField(value = "layer_id")
    private Long layerId;

//    @TableField(value = "layer_area")
//    private String layerArea;

    @NameAttachWorkspaceTrans
    private String name;

    @TableField(value = "en_name")
    private String enName;

    private String description;

    @TableField(value = "start_time_formula")
    private String startTimeFormula;

    @TableField(value = "end_time_formula")
    private String endTimeFormula;

    // 授权的名字：userName、roleName
    @TableField(value = "principal_name")
    private String principalName;

    @TableField(value = "is_available")
    private Boolean isAvailable;

    // 负责人
    private String owner;

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
