package com.webank.wedatasphere.warehouse.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
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

    private String themeAreaEn;

    private String layerArea;

    private String layerAreaEn;

    transient private Boolean status;

    private Boolean referenced;

    private int referenceCount;
}
