package com.webank.wedatasphere.warehouse.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class DwStatisticalPeriodVO {
    private Long id;

    private Long themeDomainId;

    private Long layerId;

    private String name;

    private String enName;

    private String description;

    private String startTimeFormula;

    private String endTimeFormula;

    private String principalName;

    private Boolean isAvailable;

    private String owner;

    private Date createTime;

    private Date updateTime;

    private String themeArea;

    private String layerArea;

    transient private Boolean status;
}
