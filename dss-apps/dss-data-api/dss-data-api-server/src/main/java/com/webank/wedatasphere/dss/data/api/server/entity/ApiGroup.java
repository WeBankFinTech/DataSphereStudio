package com.webank.wedatasphere.dss.data.api.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ApiGroup {
    private Integer id;
    @NotBlank(message = "name不能为空")
    private String name;
    private String note;
    private int workspaceId;
    private String createBy;

}
