package com.webank.wedatasphere.warehouse.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.wedatasphere.warehouse.dao.interceptor.NameAttachWorkspaceTrans;
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
@TableName("dss_datawarehouse_theme_domain")
public class DwThemeDomain extends DssWorkspaceEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NameAttachWorkspaceTrans
    private String name;

    @TableField(value = "en_name")
    private String enName;

    private String description;

    @TableField(value = "is_available")
    private Boolean isAvailable;

    // 可用角色，授权角色
    @TableField(value = "principal_name")
    private String principalName;

    @TableField(value = "owner")
    private String owner;

    private Integer sort;

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
