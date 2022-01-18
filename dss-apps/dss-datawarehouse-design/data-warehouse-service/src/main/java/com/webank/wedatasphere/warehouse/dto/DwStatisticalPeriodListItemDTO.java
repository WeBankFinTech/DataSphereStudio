package com.webank.wedatasphere.warehouse.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DwStatisticalPeriodListItemDTO {
    private Long id;
    private Long themeDomainId;
    private Long layerId;
    private String name;
    private String enName;
    private Boolean isAvailable;
    private String description;
    private String owner;
    private String statStartFormula;
    private String statEndFormula;
    private String principalName;
    private Date createTime;
    private Date updateTime;
}
