package com.webank.wedatasphere.warehouse.dto;

import lombok.*;

import java.util.Date;

@Data
public class DwLayerListItemDTO {
    private Long id;
    private String name;
    private String enName;
    private Boolean preset;
    private String description;
    private String principalName;
    private String dbs;
    private Integer sort;
    private Boolean isAvailable;
    private Date createTime;
    private Date updateTime;
    private int referenceCount;
}
