package com.webank.wedatasphere.dss.data.api.server.entity;

import javax.validation.constraints.NotBlank;


import lombok.Data;

@Data
public class ApiGroup {
    private Integer id;

    @NotBlank(message = "name不能为空")
    private String name;

    private String note;
    private int workspaceId;
    private String createBy;
}
