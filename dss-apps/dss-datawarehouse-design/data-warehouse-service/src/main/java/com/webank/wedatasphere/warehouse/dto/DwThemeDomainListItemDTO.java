package com.webank.wedatasphere.warehouse.dto;

import lombok.*;

import java.util.Date;

@Data
public class DwThemeDomainListItemDTO {
    private Long id;
    private String name;
    private String enName;
    private String owner;
    private String principalName;
    private Integer sort;
    private String description;
    private Date createTime;
    private Date updateTime;
    private Boolean preset;
    private Boolean isAvailable;
}
