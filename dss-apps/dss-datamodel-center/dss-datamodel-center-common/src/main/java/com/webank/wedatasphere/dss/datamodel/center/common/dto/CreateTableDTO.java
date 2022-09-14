package com.webank.wedatasphere.dss.datamodel.center.common.dto;


import lombok.Data;

@Data
public class CreateTableDTO {
    private Integer status;

    private String taskId;

    private String sql;
}
